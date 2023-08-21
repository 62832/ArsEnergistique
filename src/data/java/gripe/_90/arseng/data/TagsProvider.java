package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.api.features.P2PTunnelAttunement;

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
            var ars = tag(P2PTunnelAttunement.getAttunementTag(ArsEngItems.SOURCE_P2P_TUNNEL));

            ars.addOptionalTag(ItemTagProvider.SOURCE_GEM_TAG.location());
            ars.addOptionalTag(ItemTagProvider.SOURCE_GEM_BLOCK_TAG.location());
            ars.addOptionalTag(ItemTagProvider.ARCHWOOD_LOG_TAG.location());

            ars.addOptionalTag(BlockTagProvider.DECORATIVE_AN.location());
            ars.addOptionalTag(BlockTagProvider.MAGIC_SAPLINGS.location());
            ars.addOptionalTag(BlockTagProvider.MAGIC_PLANTS.location());
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
        }
    }
}
