package gripe._90.arseng.data;

import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.api.features.P2PTunnelAttunement;

import gripe._90.arseng.ArsEnergistique;
import gripe._90.arseng.item.ArsEngItems;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
    public ItemTagsProvider(DataGenerator gen, @Nullable ExistingFileHelper efh) {
        super(gen, new BlockTagsProvider(gen, efh), ArsEnergistique.MODID, efh);
    }

    @Override
    protected void addTags() {
        var ars = P2PTunnelAttunement.getAttunementTag(ArsEngItems.SOURCE_P2P_TUNNEL::get);

        tag(ars).addOptionalTag(ItemTagProvider.SOURCE_GEM_TAG.location());
        tag(ars).addOptionalTag(ItemTagProvider.SOURCE_GEM_BLOCK_TAG.location());
        tag(ars).addOptionalTag(ItemTagProvider.ARCHWOOD_LOG_TAG.location());

        tag(ars).addOptionalTag(BlockTagProvider.DECORATIVE_AN.location());
        tag(ars).addOptionalTag(BlockTagProvider.MAGIC_SAPLINGS.location());
        tag(ars).addOptionalTag(BlockTagProvider.MAGIC_PLANTS.location());
    }

    private static class BlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {
        public BlockTagsProvider(DataGenerator gen, @Nullable ExistingFileHelper efh) {
            super(gen, ArsEnergistique.MODID, efh);
        }
    }
}
