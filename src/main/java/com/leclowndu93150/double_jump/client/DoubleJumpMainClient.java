package com.leclowndu93150.double_jump.client;

import net.fabricmc.api.ClientModInitializer;

public class DoubleJumpMainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientEvents.registerKeyHandlers();
        DoubleJumpClientNetworking.registerClientReceiver();
    }
}
