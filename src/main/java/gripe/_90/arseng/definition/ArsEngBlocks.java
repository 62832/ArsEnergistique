package gripe._90.arseng.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.block.MESourceJarBlock;
import gripe._90.arseng.block.SourceConverterBlock;

public final class ArsEngBlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(ArsEnergistique.MODID);

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static final BlockDefinition<MESourceJarBlock> ME_SOURCE_JAR =
            block("ME Source Jar", "me_source_jar", MESourceJarBlock::new);
    public static final BlockDefinition<SourceConverterBlock> SOURCE_CONVERTER =
            block("ME Source Converter", "source_acceptor", SourceConverterBlock::new);

    private static <T extends Block> BlockDefinition<T> block(
            String englishName, String id, Supplier<T> blockSupplier) {
        var block = DR.register(id, blockSupplier);
        var item = ArsEngItems.DR.registerItem(id, p -> new BlockItem(block.get(), p));
        var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
        BLOCKS.add(definition);
        return definition;
    }
}
