package gripe._90.arseng.definition;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.BlockDefinition;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.block.entity.SourceConverterBlockEntity;

public final class ArsEngBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> DR =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ArsEnergistique.MODID);

    // public static final Supplier<BlockEntityType<MESourceJarBlockEntity>> ME_SOURCE_JAR_ENTITY =
    //        create("me_source_jar", MESourceJarBlockEntity::new, ArsEngBlocks.ME_SOURCE_JAR);
    public static final Supplier<BlockEntityType<SourceConverterBlockEntity>> SOURCE_CONVERTER_ENTITY = create(
            "source_converter",
            SourceConverterBlockEntity.class,
            SourceConverterBlockEntity::new,
            ArsEngBlocks.SOURCE_CONVERTER);

    @SuppressWarnings("DataFlowIssue")
    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> create(
            String id, BlockEntityType.BlockEntitySupplier<T> supplier, BlockDefinition<?> block) {
        return DR.register(
                id, () -> BlockEntityType.Builder.of(supplier, block.block()).build(null));
    }

    @SuppressWarnings("DataFlowIssue")
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityType.BlockEntitySupplier<T> supplier,
            BlockDefinition<? extends AEBaseEntityBlock<T>> block) {
        return DR.register(id, () -> {
            var type = BlockEntityType.Builder.of(supplier, block.block()).build(null);
            block.block().setBlockEntity(entityClass, type, null, null);
            return type;
        });
    }
}
