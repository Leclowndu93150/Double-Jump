package com.leclowndu93150.double_jump.mixin;

import com.leclowndu93150.double_jump.PlayerJumpAccess;
import com.leclowndu93150.double_jump.DoubleJumpEnchantment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerJumpAccess {
    @Unique
    private boolean hasDoubleJumped = false;

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

        if (player.level().isClientSide) return;

        int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(
                DoubleJumpEnchantment.DOUBLE_JUMP,
                player.getItemBySlot(EquipmentSlot.FEET)
        );

        if (player.onGround()) {
            hasDoubleJumped = false;
            jumpKeyPressed = false;
        }

        if (enchantmentLevel > 0 && !player.onGround() && !hasDoubleJumped && jumpKeyPressed) {
            performDoubleJump(player, enchantmentLevel);
            jumpKeyPressed = false;
        }
    }

    @Unique
    private void performDoubleJump(Player player, int enchantmentLevel) {
        if (player.level() instanceof ServerLevel serverLevel) {

            /*
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    DoubleJumpEnchantment.DOUBLE_JUMP_SOUND.get(),
                    SoundSource.PLAYERS,
                    0.3F,
                    2.0F
            );
            */

            for (int i = 0; i < 20; ++i) {
                double offsetX = serverLevel.random.nextGaussian() * 0.02D;
                double offsetY = serverLevel.random.nextGaussian() * 0.02D;
                double offsetZ = serverLevel.random.nextGaussian() * 0.02D;

                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        player.getX() + (player.getRandom().nextFloat() * player.getBbWidth() * 2.0F) - player.getBbWidth(),
                        player.getY(),
                        player.getZ() + (player.getRandom().nextFloat() * player.getBbWidth() * 2.0F) - player.getBbWidth(),
                        1,
                        offsetX,
                        offsetY,
                        offsetZ,
                        0.0D
                );
            }
        }

        double jumpBoost = switch (enchantmentLevel) {
            case 2 -> 0.5;
            case 3 -> 0.75;
            default -> 0.3;
        };

        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x(), motion.y() + jumpBoost, motion.z());
        System.out.println("Double Jumped");
        hasDoubleJumped = true;
    }
}
