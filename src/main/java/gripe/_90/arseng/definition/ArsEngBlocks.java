package gripe._90.arseng.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import appeng.core.definitions.BlockDefinition;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.block.MESourceJarBlock;
import gripe._90.arseng.block.SourceAcceptorBlock;
import gripe._90.arseng.block.entity.MESourceJarBlockEntity;
import gripe._90.arseng.block.entity.SourceAcceptorBlockEntity;

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

    public static final BlockDefinition<MESourceJarBlock> ME_SOURCE_JAR =
            block("ME Source Jar", "me_source_jar", MESourceJarBlock::new);
    public static final BlockEntityType<MESourceJarBlockEntity> ME_SOURCE_JAR_ENTITY =
            blockEntity("me_source_jar", MESourceJarBlockEntity::new, ME_SOURCE_JAR);

    public static final BlockDefinition<SourceAcceptorBlock> SOURCE_ACCEPTOR =
            block("ME Source Converter", "source_acceptor", SourceAcceptorBlock::new);
    public static final BlockEntityType<SourceAcceptorBlockEntity> SOURCE_ACCEPTOR_ENTITY = Util.make(() -> {
        var type = blockEntity("source_acceptor", SourceAcceptorBlockEntity::new, SOURCE_ACCEPTOR);
        SOURCE_ACCEPTOR.block().setBlockEntity(SourceAcceptorBlockEntity.class, type, null, null);
        return type;
    });

    private static <T extends Block> BlockDefinition<T> block(
            String englishName, String id, Supplier<T> blockSupplier) {
        var block = blockSupplier.get();
        var item = new BlockItem(block, new Item.Properties());
        var definition = new BlockDefinition<>(englishName, ArsEnergistique.makeId(id), block, item);
        BLOCKS.add(definition);
        return definition;
    }

    @SuppressWarnings("DataFlowIssue")
    private static <T extends BlockEntity> BlockEntityType<T> blockEntity(
            String id, BlockEntityType.BlockEntitySupplier<T> supplier, BlockDefinition<?> block) {
        var type = BlockEntityType.Builder.of(supplier, block.block()).build(null);
        BLOCK_ENTITIES.put(ArsEnergistique.makeId(id), type);
        return type;
    }
}
