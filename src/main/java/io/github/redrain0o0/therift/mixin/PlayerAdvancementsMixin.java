package io.github.redrain0o0.therift.mixin;

import io.github.redrain0o0.therift.Therift;
import io.github.redrain0o0.therift.event.PlayerAdvancementCallback;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {

    @Shadow
    private ServerPlayer player;

    @Inject(method = "Lnet/minecraft/server/PlayerAdvancements;method_53637(Lnet/minecraft/advancements/AdvancementHolder;Lnet/minecraft/advancements/DisplayInfo;)V", at = @At(target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", value = "INVOKE"))
    private void Therift$award(AdvancementHolder advancementHolder, DisplayInfo displayInfo, CallbackInfo ci) {
        PlayerAdvancementCallback.EVENT.invoker().onAdvancementGranted(player, advancementHolder.value());
    }
}