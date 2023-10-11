package gripe._90.arseng.me.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import appeng.api.client.IAEStackRenderHandler;
import appeng.client.gui.style.Blitter;

import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;

@OnlyIn(Dist.CLIENT)
public class SourceRenderer implements IAEStackRenderHandler<SourceKey> {
    public static final SourceRenderer INSTANCE = new SourceRenderer();

    private SourceRenderer() {}

    public static final Material SOURCE =
            new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("ars_nouveau", "blocks/mana_still"));

    @Override
    public void drawInGui(Minecraft minecraft, PoseStack poseStack, int x, int y, int zIndex, SourceKey stack) {
        Blitter.sprite(SOURCE.sprite())
                // Most fluid texture have transparency, but we want an opaque slot
                .blending(false)
                .dest(x, y, 16, 16)
                .blit(poseStack, 100 + zIndex);
    }

    @SuppressWarnings("resource")
    @Override
    public void drawOnBlockFace(
            PoseStack poseStack, MultiBufferSource buffers, SourceKey what, float scale, int combinedLight) {
        var sprite = SOURCE.sprite();
        poseStack.pushPose();
        // Push it out of the block face a bit to avoid z-fighting
        poseStack.translate(0, 0, 0.01f);

        var buffer = buffers.getBuffer(RenderType.solid());

        // In comparison to items, make it _slightly_ smaller because item icons
        // usually don't extend to the full size.
        scale -= 0.05f;

        // y is flipped here
        var x0 = -scale / 2;
        var y0 = scale / 2;
        var x1 = scale / 2;
        var y1 = -scale / 2;

        var transform = poseStack.last().pose();
        buffer.vertex(transform, x0, y1, 0)
                .color(-1)
                .uv(sprite.getU0(), sprite.getV1())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        buffer.vertex(transform, x1, y1, 0)
                .color(-1)
                .uv(sprite.getU1(), sprite.getV1())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        buffer.vertex(transform, x1, y0, 0)
                .color(-1)
                .uv(sprite.getU1(), sprite.getV0())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        buffer.vertex(transform, x0, y0, 0)
                .color(-1)
                .uv(sprite.getU0(), sprite.getV0())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();

        poseStack.popPose();
    }

    @Override
    public Component getDisplayName(SourceKey stack) {
        return SourceKeyType.SOURCE;
    }
}
