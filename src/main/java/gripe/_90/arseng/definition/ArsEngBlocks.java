package gripe._90.arseng.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;

import gripe._90.arseng.block.SourceAcceptorBlock;
import gripe._90.arseng.block.SourceAcceptorBlockEntity;

public final class ArsEngBlocks {
    private ArsEngBlocks() {}

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITIES = new HashMap<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            BLOCKS.forEach(b -> ForgeRegistries.BLOCKS.register(b.id(), b.block()));
        }

        if (event.getRegistryKey().equals(Registries.ITEM)) {
            BLOCKS.forEach(b -> ForgeRegistries.ITEMS.register(b.id(), b.asItem()));
        }

        if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            BLOCK_ENTITIES.forEach(ForgeRegistries.BLOCK_ENTITY_TYPES::register);
        }
    }

    public static final BlockDefinition<SourceAcceptorBlock> SOURCE_ACCEPTOR =
            block("ME Source Acceptor", "source_acceptor", SourceAcceptorBlock::new);

    public static final BlockEntityType<SourceAcceptorBlockEntity> SOURCE_ACCEPTOR_ENTITY =
            blockEntity("source_acceptor", SourceAcceptorBlockEntity::new, SOURCE_ACCEPTOR);

    private static <T extends Block> BlockDefinition<T> block(String englishName, String id, Supplier<T> supplier) {
        return block(englishName, id, supplier, block -> new AEBaseBlockItem(block, new Item.Properties()));
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            Function<T, ? extends AEBaseBlockItem> itemFunction) {
        var block = blockSupplier.get();
        var item = itemFunction.apply(block);
        var definition = new BlockDefinition<>(englishName, ArsEngCore.makeId(id), block, item);
        BLOCKS.add(definition);
        return definition;
    }

    private static <T extends BlockEntity> BlockEntityType<T> blockEntity(
            String id, BlockEntityType.BlockEntitySupplier<T> supplier, BlockDefinition<? extends Block> block) {
        var type = BlockEntityType.Builder.of(supplier, block.block()).build(null);
        BLOCK_ENTITIES.put(ArsEngCore.makeId(id), type);
        return type;
    }
}
