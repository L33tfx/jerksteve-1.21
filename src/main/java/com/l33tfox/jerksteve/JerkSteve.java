package com.l33tfox.jerksteve;

import com.l33tfox.jerksteve.block.custom.JerkSteveCameraBlock;
import com.l33tfox.jerksteve.entity.custom.JerkSteveEntity;
import com.l33tfox.jerksteve.item.ModItemsRegistry;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JerkSteve implements ModInitializer {
	public static final String MOD_ID = "jerksteve";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<JerkSteveEntity> JERKSTEVE = Registry.register(Registries.ENTITY_TYPE,
			Identifier.of(MOD_ID, "jerksteve"),
			EntityType.Builder.create(JerkSteveEntity::new, SpawnGroup.MISC).dimensions(0.6f, 1.8f).build());
	public static final Block JERKSTEVE_CAMERA = registerBlock("jerksteve_camera",
			new JerkSteveCameraBlock(AbstractBlock.Settings.create().nonOpaque().requiresTool().strength(1.5F, 6.0F)));

	@Override
	public void onInitialize() {
		ModItemsRegistry.initialize();
		FabricDefaultAttributeRegistry.register(JERKSTEVE, JerkSteveEntity.createAttributes());

	}

	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, name), block);
	}

	private static void registerBlockItem(String name, Block block) {
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, name), new BlockItem(block, new Item.Settings()));
	}
}