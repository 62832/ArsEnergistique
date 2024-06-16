package gripe._90.arseng.client;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

import appeng.client.render.cablebus.CubeBuilder;

import gripe._90.arseng.block.entity.MESourceJarBlockEntity;

public class MESourceJarBlockEntityRenderer implements BlockEntityRenderer<MESourceJarBlockEntity> {
    public MESourceJarBlockEntityRenderer(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(
            @NotNull MESourceJarBlockEntity jar,
            float partialTicks,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffers,
            int light,
            int overlay) {
        var amount = (float) jar.getSource() / jar.getMaxSource();

        if (amount <= 0) {
            return;
        }

        var vertexConsumer = buffers.getBuffer(RenderType.translucentMovingBlock());
        var fill = Mth.lerp(Mth.clamp(amount, 0, 1), 2 / 16F, 12 / 16F);

        var builder = new CubeBuilder();
        builder.setTexture(SourceRenderer.SOURCE.sprite());
        builder.addCube(4, 2, 4, 12, fill * 16, 12);

        for (var bakedQuad : builder.getOutput()) {
            vertexConsumer.putBulkData(poseStack.last(), bakedQuad, 1, 1, 1, light, overlay);
        }
    }
}
