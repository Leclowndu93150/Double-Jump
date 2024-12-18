package com.leclowndu93150.double_jump;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DoubleJumpNetworking {
    public static final ResourceLocation JUMP_PACKET_ID = new ResourceLocation("double_jump", "jump_pressed");
    public static final ResourceLocation CLIENT_JUMP_PACKET_ID = new ResourceLocation("double_jump", "client_jump_pressed");


    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(JUMP_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            boolean jumpKeyPressed = buf.readBoolean();
            server.execute(() -> {
                if (player instanceof ServerPlayer) {
                    ((PlayerJumpAccess) player).setJumpKeyPressed(jumpKeyPressed);
                }
            });
        });
    }

    public static void sendJumpBoostPacket(ServerPlayer player, double jumpBoost) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeDouble(jumpBoost);
        ServerPlayNetworking.send(player, CLIENT_JUMP_PACKET_ID, buf);
    }
}
