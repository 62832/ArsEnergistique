package gripe._90.arseng.definition;

import appeng.blockentity.powersink.IExternalPowerSink;
import gripe._90.arseng.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ArsEngBlocks{

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArsEngCore.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArsEngCore.MODID);

    static BlockBehaviour.Properties basicProperties = BlockBehaviour.Properties.of().destroyTime(2f).sound(SoundType.STONE);
    public static final RegistryObject<Block> SOURCE_ACCEPTOR_BLOCK = BLOCKS.register("source_acceptor_block", () -> new SourceAcceptorBlock(basicProperties));

    public static final RegistryObject<BlockEntityType<SourceAcceptorBlockEntity>> SOURCE_ACCEPTOR_TYPE = BLOCK_ENTITIES.register("source_acceptor_block",() -> BlockEntityType.Builder.of(SourceAcceptorBlockEntity::new,SOURCE_ACCEPTOR_BLOCK.get()).build(null));


    public static void DoSetup(IEventBus bus){
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }

}
