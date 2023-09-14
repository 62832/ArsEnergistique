package gripe._90.arseng.data;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import appeng.core.definitions.BlockDefinition;

import gripe._90.arseng.definition.ArsEngBlocks;

public class BlockDropProvider extends LootTableProvider {
    protected BlockDropProvider(DataGenerator generator) {
        super(generator);
    }

    @NotNull
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>>
            getTables() {
        return ImmutableList.of(Pair.of(
                () -> new BlockLoot() {
                    @Override
                    protected void addTables() {
                        for (var block : getKnownBlocks()) {
                            add(
                                    block,
                                    LootTable.lootTable()
                                            .withPool(LootPool.lootPool()
                                                    .setRolls(ConstantValue.exactly(1))
                                                    .add(LootItem.lootTableItem(block))
                                                    .when(ExplosionCondition.survivesExplosion())));
                        }
                    }

                    @NotNull
                    @Override
                    protected Iterable<Block> getKnownBlocks() {
                        return ArsEngBlocks.getBlocks().stream()
                                .map(BlockDefinition::block)
                                .map(Block.class::cast)::iterator;
                    }
                },
                LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationTracker) {
        map.forEach((id, table) -> LootTables.validate(validationTracker, id, table));
    }
}
