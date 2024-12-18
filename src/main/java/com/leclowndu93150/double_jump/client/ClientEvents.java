package com.leclowndu93150.double_jump.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class ClientEvents {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static boolean lastJumpState = false;

    public static void registerKeyHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                boolean isJumpKeyPressed = client.options.keyJump.isDown();
                if (isJumpKeyPressed != lastJumpState) {
                    DoubleJumpClientNetworking.sendJumpKeyState(isJumpKeyPressed);
                    lastJumpState = isJumpKeyPressed;
                }
            }
        });
    }
}
