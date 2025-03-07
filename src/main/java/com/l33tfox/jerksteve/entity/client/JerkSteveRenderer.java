package com.l33tfox.jerksteve.entity.client;

import com.l33tfox.jerksteve.JerkSteve;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class JerkSteveRenderer extends BipedEntityRenderer<JerkSteveEntity, JerkSteveModel<JerkSteveEntity>> {

    public JerkSteveRenderer(EntityRendererFactory.Context context) {
        super(context, new JerkSteveModel<>(context.getPart(JerkSteveModel.JERKSTEVE)), 0.5f);
    }

    @Override
    public Identifier getTexture(JerkSteveEntity entity) {
        return Identifier.of(JerkSteve.MOD_ID, "textures/entity/jerksteve/steve_texture.png");
    }

    // render the entity in game
    @Override
    public void render(JerkSteveEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
