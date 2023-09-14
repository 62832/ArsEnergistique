package gripe._90.arseng.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;

import appeng.api.features.P2PTunnelAttunement;
import appeng.datagen.providers.tags.ConventionTags;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;

abstract class TagsProvider {
    static class Items extends ItemTagsProvider {
        Items(
                PackOutput output,
                CompletableFuture<HolderLookup.Provider> registries,
                CompletableFuture<TagLookup<Block>> blockTags) {
            super(output, registries, blockTags, ArsEngCore.MODID, null);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            var sourceP2P = tag(P2PTunnelAttunement.getAttunementTag(ArsEngItems.SOURCE_P2P_TUNNEL));
            sourceP2P
                    .addOptionalTag(ItemTagProvider.SOURCE_GEM_TAG.location())
                    .addOptionalTag(ItemTagProvider.SOURCE_GEM_BLOCK_TAG.location())
                    .addOptionalTag(ItemTagProvider.ARCHWOOD_LOG_TAG.location())
                    .addOptionalTag(BlockTagProvider.DECORATIVE_AN.location())
                    .addOptionalTag(BlockTagProvider.MAGIC_SAPLINGS.location())
                    .addOptionalTag(BlockTagProvider.MAGIC_PLANTS.location());

            var spellP2P = tag(P2PTunnelAttunement.getAttunementTag(ArsEngItems.SPELL_P2P_TUNNEL));
            spellP2P.add(
                    ItemsRegistry.NOVICE_SPELLBOOK.asItem(),
                    ItemsRegistry.APPRENTICE_SPELLBOOK.asItem(),
                    ItemsRegistry.ARCHMAGE_SPELLBOOK.asItem(),
                    ItemsRegistry.CREATIVE_SPELLBOOK.asItem(),
                    BlockRegistry.SPELL_PRISM.asItem(),
                    BlockRegistry.VOID_PRISM.asItem(),
                    ItemsRegistry.SPELL_PARCHMENT.asItem(),
                    ItemsRegistry.SPELL_BOW.asItem(),
                    ItemsRegistry.SPELL_CROSSBOW.asItem());

            tag(ItemTagProvider.SHARD_TAG).addOptionalTag(ConventionTags.CERTUS_QUARTZ.location());
        }
    }

    static class Blocks extends BlockTagsProvider {
        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, ArsEngCore.MODID, null);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            ArsEngBlocks.getBlocks()
                    .forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block()));

            tag(BlockTagProvider.BUDDING_BLOCKS).addOptionalTag(ConventionTags.BUDDING_BLOCKS_BLOCKS.location());
            tag(BlockTagProvider.CLUSTER_BLOCKS).addOptionalTag(ConventionTags.CLUSTERS_BLOCKS.location());
        }
    }
}
