package com.leclowndu93150.double_jump.client;

import com.leclowndu93150.double_jump.DoubleJumpNetworking;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class DoubleJumpClientNetworking {
    public static void sendJumpKeyState(boolean isPressed) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()));
        buf.writeBoolean(isPressed);
        ClientPlayNetworking.send(DoubleJumpNetworking.JUMP_PACKET_ID, buf);
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(DoubleJumpNetworking.CLIENT_JUMP_PACKET_ID, (client, handler, buf, responseSender) -> {
            double jumpBoost = buf.readDouble();
            client.execute(() -> {
                if(client.player != null){
                    Vec3 motion = client.player.getDeltaMovement();
                    client.player.setDeltaMovement(motion.x, jumpBoost, motion.z);
                }
            });
        });
    }
}
