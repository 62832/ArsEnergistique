package gripe._90.arseng.data;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import appeng.core.definitions.BlockDefinition;

import gripe._90.arseng.definition.ArsEngBlocks;

public class BlockDropProvider extends BlockLootSubProvider {
    protected BlockDropProvider() {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS);
    }

    @Override
    protected void generate() {
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
        return ArsEngBlocks.getBlocks().stream().map(BlockDefinition::block).map(Block.class::cast)::iterator;
    }
}
