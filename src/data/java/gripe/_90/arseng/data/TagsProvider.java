package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.api.features.P2PTunnelAttunement;
import appeng.datagen.providers.tags.ConventionTags;

import gripe._90.arseng.definition.ArsEngBlocks;
import gripe._90.arseng.definition.ArsEngCore;
import gripe._90.arseng.definition.ArsEngItems;

class TagsProvider {
    static class Items extends ItemTagsProvider {
        public Items(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper existing) {
            super(generator, blockTags, ArsEngCore.MODID, existing);
        }

        @Override
        protected void addTags() {
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
        public Blocks(DataGenerator generator, ExistingFileHelper existing) {
            super(generator, ArsEngCore.MODID, existing);
        }

        @Override
        protected void addTags() {
            ArsEngBlocks.getBlocks()
                    .forEach(block -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.block()));

            tag(BlockTagProvider.BUDDING_BLOCKS).addOptionalTag(ConventionTags.BUDDING_BLOCKS_BLOCKS.location());
            tag(BlockTagProvider.CLUSTER_BLOCKS).addOptionalTag(ConventionTags.CLUSTERS_BLOCKS.location());
        }
    }
}
