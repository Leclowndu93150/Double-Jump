package com.leclowndu93150.double_jump.mixin;

import com.leclowndu93150.double_jump.ExperienceConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow public abstract int getValue();

    @ModifyArg(
            method = "playerTouch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;giveExperiencePoints(I)V"
            )
    )
    private int modifyExperienceValue(int value) {
        ExperienceOrb orb = (ExperienceOrb) (Object) this;

        if (!(orb.level() instanceof ServerLevel serverLevel)) {
            return value;
        }

        String dimensionKey = serverLevel.dimension().location().toString();
        double multiplier = ExperienceConfig.load().getMultiplier(dimensionKey);

        return (int) (value * multiplier);
    }
}