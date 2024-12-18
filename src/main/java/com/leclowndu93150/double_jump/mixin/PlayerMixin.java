package com.leclowndu93150.double_jump.mixin;

import com.leclowndu93150.double_jump.PlayerJumpAccess;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import com.leclowndu93150.double_jump.DoubleJumpEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerJumpAccess {
    @Unique
    private boolean hasPerformedDoubleJump = false;

    @Unique
    private boolean jumpKeyPressed = false;

    @Override
    public void setJumpKeyPressed(boolean pressed) {
        this.jumpKeyPressed = pressed;
    }

    @Override
    public boolean isJumpKeyPressed() {
        return this.jumpKeyPressed;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player) (Object) this;

        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(
                DoubleJumpEnchantment.DOUBLE_JUMP,
                player.getItemBySlot(EquipmentSlot.FEET)
        );

        if (player.onGround()) {
            hasPerformedDoubleJump = false;
        }

        if (enchantLevel > 0 && !player.onGround() && !hasPerformedDoubleJump && jumpKeyPressed) {
            performDoubleJump(player, enchantLevel);
            System.out.println("Performed double jump");
            jumpKeyPressed = false;
        }
    }

    @Unique
    private void performDoubleJump(Player player, int enchantLevel) {
        double heightBoost = switch (enchantLevel) {
            case 2 -> 1.5;
            case 3 -> 2.0;
            default -> 1.0;
        };

        player.setDeltaMovement(
                player.getDeltaMovement().x,
                heightBoost,
                player.getDeltaMovement().z
        );

        hasPerformedDoubleJump = true;
    }
}
