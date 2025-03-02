package com.l33tfox.jerksteve;

import com.l33tfox.jerksteve.entity.client.JerkSteveModel;
import com.l33tfox.jerksteve.entity.client.JerkSteveRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class JerkSteveClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(JerkSteveModel.JERKSTEVE, JerkSteveModel::getTexturedModelData);
        EntityRendererRegistry.register(JerkSteve.JERKSTEVE, JerkSteveRenderer::new);
    }
}
