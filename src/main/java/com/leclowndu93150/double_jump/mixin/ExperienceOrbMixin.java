package com.leclowndu93150.double_jump.mixin;

import com.leclowndu93150.double_jump.ExperienceConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void modifyExperience(Player player, CallbackInfo ci) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        String dimensionKey = serverLevel.dimension().location().toString();

        double multiplier = ExperienceConfig.load().getMultiplier(dimensionKey);

        ExperienceOrb orb = (ExperienceOrb) (Object) this;
        int originalValue = orb.getValue();

        int modifiedValue = (int) (originalValue * multiplier);

        player.giveExperiencePoints(modifiedValue);

        orb.count--;
        if (orb.count <= 0) {
            orb.discard();
        }

        ci.cancel();
    }
}
