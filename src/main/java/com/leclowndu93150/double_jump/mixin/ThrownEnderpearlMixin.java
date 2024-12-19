package com.leclowndu93150.double_jump.mixin;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.UUID;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin {

    @Unique
    private static final double BOUNCE_DAMPING = 0.7;
    @Unique
    private static final double MIN_VELOCITY = 0.2;
    @Unique
    private static final double WALL_BOUNCE_DAMPING = 0.8;

    private static final HashMap<UUID, ThrownEnderpearl> ACTIVE_PEARLS = new HashMap<>();

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;
        if (self.getOwner() instanceof Player) {
            Player player = (Player) self.getOwner();
            if (ACTIVE_PEARLS.containsKey(player.getUUID())) {
                self.discard();
                player.sendSystemMessage(Component.literal("You must wait for your current ender pearl to finish!"));
            } else {
                ACTIVE_PEARLS.put(player.getUUID(), self);
            }
        }
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void onHit(HitResult hitResult, CallbackInfo ci) {
        ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;
        Level level = self.level();

        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            Direction hitDirection = blockHitResult.getDirection();
            Vec3 velocity = self.getDeltaMovement();

            if (velocity.lengthSqr() > MIN_VELOCITY * MIN_VELOCITY) {
                level.playSound(null, self.getX(), self.getY(), self.getZ(),
                        SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 2.0F, 1.0F);

                Vec3 newVelocity;
                if (hitDirection == Direction.UP || hitDirection == Direction.DOWN) {
                    newVelocity = new Vec3(velocity.x, -velocity.y * BOUNCE_DAMPING, velocity.z);
                } else if (hitDirection == Direction.NORTH || hitDirection == Direction.SOUTH) {
                    newVelocity = new Vec3(velocity.x, velocity.y, -velocity.z * WALL_BOUNCE_DAMPING);
                } else {
                    newVelocity = new Vec3(-velocity.x * WALL_BOUNCE_DAMPING, velocity.y, velocity.z);
                }

                self.setDeltaMovement(newVelocity);
                ci.cancel();
            } else {
                if (self.getOwner() instanceof Player) {
                    Player player = (Player) self.getOwner();
                    ACTIVE_PEARLS.remove(player.getUUID());
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;

        if (!self.isAlive() && self.getOwner() instanceof Player) {
            Player player = (Player) self.getOwner();
            ACTIVE_PEARLS.remove(player.getUUID());
        }
    }
}