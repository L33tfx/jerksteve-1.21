package com.l33tfox.jerksteve.item;

import com.l33tfox.jerksteve.JerkSteve;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItemsRegistry {

    public static final Item JERKSTEVE_SPAWN_EGG = register("jerksteve_spawn_egg", new SpawnEggItem(JerkSteve.JERKSTEVE,
                                                0x00AFAF, 0xBE886C, new Item.Settings()));

    public static void initialize() {
        // get the event for modifying entries in the tools group and register an event handler that adds the mod items.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> {itemGroup.add(JERKSTEVE_SPAWN_EGG);});

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((itemGroup) -> {itemGroup.add(JerkSteve.JERKSTEVE_CAMERA);});
    }

    // helper method for registering new mod items
    private static Item register(String id, Item item) {
        // create the identifier for the item
        Identifier itemID = Identifier.of(JerkSteve.MOD_ID, id);

        // register and return the item
        return Registry.register(Registries.ITEM, itemID, item);
    }
}
