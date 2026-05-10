package com.modpackauthors.fabric.mixin;

import com.modpackauthors.client.menu.MainMenuButtonInjector;
import com.modpackauthors.util.Components;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin() {
        super(Components.empty());
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void modpackAuthors$init(CallbackInfo callbackInfo) {
        MainMenuButtonInjector.inject((TitleScreen) (Object) this, this.children, this::addButton);
    }
}
