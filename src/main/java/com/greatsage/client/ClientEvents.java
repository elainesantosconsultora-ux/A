package com.greatsage.client;

import com.greatsage.GreatSageMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {
    public static final KeyMapping OPEN_MENU = new KeyMapping("key.greatsage.open",
            InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_4, "key.categories.greatsage");
    public static final KeyMapping OPEN_MENU_ALT = new KeyMapping("key.greatsage.open_alt",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.categories.greatsage");

    @Mod.EventBusSubscriber(modid = GreatSageMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e) {
            e.register(OPEN_MENU); e.register(OPEN_MENU_ALT);
        }
    }
    @Mod.EventBusSubscriber(modid = GreatSageMod.MOD_ID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;
            while (OPEN_MENU.consumeClick() | OPEN_MENU_ALT.consumeClick())
                mc.setScreen(new GreatSageScreen());
        }
    }
}
