package gripe._90.arseng.definition;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.BlockDefinition;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.block.entity.MESourceJarBlockEntity;
import gripe._90.arseng.block.entity.SourceConverterBlockEntity;

public final class ArsEngBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> DR =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ArsEnergistique.MODID);

    public static final Supplier<BlockEntityType<MESourceJarBlockEntity>> ME_SOURCE_JAR = create(
            "me_source_jar", MESourceJarBlockEntity.class, MESourceJarBlockEntity::new, ArsEngBlocks.ME_SOURCE_JAR);
    public static final Supplier<BlockEntityType<SourceConverterBlockEntity>> SOURCE_CONVERTER = create(
            "source_converter",
            SourceConverterBlockEntity.class,
            SourceConverterBlockEntity::new,
            ArsEngBlocks.SOURCE_CONVERTER);

    @SuppressWarnings("DataFlowIssue")
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityType.BlockEntitySupplier<T> supplier,
            BlockDefinition<? extends AEBaseEntityBlock<T>> block) {
        return DR.register(id, () -> {
            var type = BlockEntityType.Builder.of(supplier, block.block()).build(null);

            BlockEntityTicker<T> clientTicker = null;
            BlockEntityTicker<T> serverTicker = null;

            if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
            }

            if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
            }

            block.block().setBlockEntity(entityClass, type, clientTicker, serverTicker);
            AEBaseBlockEntity.registerBlockEntityItem(type, block.asItem());
            return type;
        });
    }
}
