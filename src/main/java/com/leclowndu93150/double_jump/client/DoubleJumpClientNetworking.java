package com.leclowndu93150.double_jump.client;

import com.leclowndu93150.double_jump.DoubleJumpNetworking;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleJumpClientNetworking {
    public static void sendJumpKeyState(boolean isPressed) {
        FriendlyByteBuf buf = new FriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()));
        buf.writeBoolean(isPressed);
        ClientPlayNetworking.send(DoubleJumpNetworking.JUMP_PACKET_ID, buf);
        System.out.println("Sent jump key state to server: " + isPressed);
    }
}
