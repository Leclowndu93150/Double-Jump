package com.leclowndu93150.double_jump;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DoubleJumpEnchantment extends Enchantment {
    public static final DoubleJumpEnchantment DOUBLE_JUMP = new DoubleJumpEnchantment(
            Rarity.RARE,
            EnchantmentCategory.ARMOR_FEET,
            new EquipmentSlot[] {EquipmentSlot.FEET}
    );

    public DoubleJumpEnchantment(Rarity rarity, EnchantmentCategory target, EquipmentSlot[] slotTypes) {
        super(rarity, target, slotTypes);
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }
}