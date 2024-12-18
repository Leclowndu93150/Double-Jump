package com.leclowndu93150.double_jump;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class DoubleJumpMain implements ModInitializer {

    public static final DoubleJumpConfig CONFIG = DoubleJumpConfig.load();

    @Override
    public void onInitialize() {
        DoubleJumpNetworking.registerServerReceiver();
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("double_jump", "double_jump"),
                DoubleJumpEnchantment.DOUBLE_JUMP
        );

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            for (String lootTableStr : CONFIG.lootTables.keySet()) {
                ResourceLocation lootTableId = ResourceLocation.tryParse(lootTableStr);
                if (id.equals(lootTableId)) {
                    Float chance = CONFIG.lootTables.get(lootTableStr) / 100f;

                    LootPool.Builder poolBuilder = LootPool.lootPool()
                            .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                                    .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(1, 1))
                                            .allowTreasure()
                                    )
                            )
                            .when(LootItemRandomChanceCondition.randomChance(chance));

                    tableBuilder.pool(poolBuilder.build());
                }
            }
        });
    }
}
