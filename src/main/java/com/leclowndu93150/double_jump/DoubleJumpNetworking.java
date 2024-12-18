package com.leclowndu93150.double_jump;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DoubleJumpNetworking {
    public static final ResourceLocation JUMP_PACKET_ID = new ResourceLocation("double_jump", "jump_pressed");

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(JUMP_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            boolean jumpKeyPressed = buf.readBoolean();
            server.execute(() -> {
                if (player instanceof ServerPlayer) {
                    ((PlayerJumpAccess) player).setJumpKeyPressed(jumpKeyPressed);
                    System.out.println("Received jump key state from client: " + jumpKeyPressed);
                }
            });
        });
    }
}
