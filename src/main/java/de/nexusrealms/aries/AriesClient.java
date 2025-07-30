package de.nexusrealms.aries;

import io.github.apace100.apoli.util.keybinding.KeyBindingUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AriesClient implements ClientModInitializer {
    public static KeyBinding primaryActiveKeyBinding;
    public static KeyBinding secondaryActiveKeyBinding;
    public static KeyBinding tertiaryActiveKeyBinding;
    public static KeyBinding quaternaryActiveKeyBinding;
    @Override
    public void onInitializeClient() {
        primaryActiveKeyBinding = new KeyBinding("key.aries.primary_active", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category." + Aries.MOD_ID);
        secondaryActiveKeyBinding = new KeyBinding("key.aries.secondary_active", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category." + Aries.MOD_ID);
        tertiaryActiveKeyBinding = new KeyBinding("key.aries.tertiary_active", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category." + Aries.MOD_ID);
        quaternaryActiveKeyBinding = new KeyBinding("key.aries.quaternary_active", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category." + Aries.MOD_ID);

        KeyBindingUtil.ALIASES.addAlias("primary", primaryActiveKeyBinding.getTranslationKey());
        KeyBindingUtil.ALIASES.addAlias("secondary", secondaryActiveKeyBinding.getTranslationKey());
        KeyBindingUtil.ALIASES.addAlias("tertiary", tertiaryActiveKeyBinding.getTranslationKey());
        KeyBindingUtil.ALIASES.addAlias("quaternary", quaternaryActiveKeyBinding.getTranslationKey());
        //  "none" is the default key used when no keybinding reference is specified in powers
        KeyBindingUtil.ALIASES.addAlias("none", primaryActiveKeyBinding.getTranslationKey());

        KeyBindingHelper.registerKeyBinding(primaryActiveKeyBinding);
        KeyBindingHelper.registerKeyBinding(secondaryActiveKeyBinding);
        KeyBindingHelper.registerKeyBinding(tertiaryActiveKeyBinding);
        KeyBindingHelper.registerKeyBinding(quaternaryActiveKeyBinding);
    }
}
