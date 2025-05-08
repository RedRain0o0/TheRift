package io.github.redrain0o0.therift;

import io.github.redrain0o0.therift.event.PlayerAdvancementCallback;
import io.github.redrain0o0.therift.util.DiscordWebhook;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.jpountz.util.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Therift implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("The Rift");
    public static final Config CONFIG = new Config();
    public static MinecraftServer SERVER = null;

    private static DiscordWebhook WEBHOOKMIKU = null;
    private static DiscordWebhook WEBHOOKRAD = null;
    private static final DiscordWebhook.EmbedObject EMBED = new DiscordWebhook.EmbedObject();


    @Override
    public void onInitializeServer() {
        try {
            if (tryInitConfig()) {
                //LOGGER.info("{}, {}, {}, {}, {}, {}, {}", CONFIG.token, CONFIG.channelIdMiku, CONFIG.channelIdRad, CONFIG.webhookMiku, CONFIG.webhookRad, CONFIG.serverName, CONFIG.serverIconUrl);
                WEBHOOKMIKU = new DiscordWebhook(CONFIG.webhookMiku);
                WEBHOOKRAD = new DiscordWebhook(CONFIG.webhookRad);
                ServerLifecycleEvents.SERVER_STARTED.register(this::serverStart);
                ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
                ServerMessageEvents.CHAT_MESSAGE.register(this::onPlayerChat);
                ServerMessageEvents.COMMAND_MESSAGE.register(this::onCommand);
                ServerMessageEvents.GAME_MESSAGE.register(this::onGameMessage);
                ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerLeave);
                ServerLifecycleEvents.SERVER_STOPPING.register(this::serverShutdown);
                PlayerAdvancementCallback.EVENT.register(this::onPlayerAdvancement);
                try {
                    BotHandler.create(CONFIG);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Login Failed! Please update your config and restart the server");
        }
    }

    private boolean tryInitConfig() throws IOException {
        File file = new File(FabricLoader.getInstance().getConfigDir() + "/rift.json");
        if (!file.exists()) {
            CONFIG.writeConfig(file);
            LOGGER.error(
                    String.format(
                            "Config not found! Generated template at %s",
                            file.getAbsolutePath()
                    )
            );
            LOGGER.error("Please update your config and restart the server");
            return false;
        }

        CONFIG.readConfig(file);
        return true;
    }

    public void serverStart(MinecraftServer server) {
        LOGGER.info("Server {} started, notify webhooks", server.getPort());
        SERVER = server;
        WEBHOOKMIKU.setUsername(CONFIG.serverName);
        WEBHOOKRAD.setUsername(CONFIG.serverName);
        WEBHOOKMIKU.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKRAD.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKMIKU.setContent("Server has started!");
        WEBHOOKRAD.setContent("Server has started!");
        try {
            WEBHOOKMIKU.execute();
            WEBHOOKRAD.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        //if (handler.player != null)
        LOGGER.info("{} joined the server, notify webhooks", handler.player.getName().getString());
        WEBHOOKMIKU.setUsername(CONFIG.serverName);
        WEBHOOKRAD.setUsername(CONFIG.serverName);
        WEBHOOKMIKU.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKRAD.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKMIKU.setContent(handler.player.getName().getString() + " has joined the game!");
        WEBHOOKRAD.setContent(handler.player.getName().getString() + " has joined the game!");
        try {
            WEBHOOKMIKU.execute();
            WEBHOOKRAD.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onPlayerChat(PlayerChatMessage playerChatMessage, ServerPlayer serverPlayer, ChatType.Bound bound) {
        LOGGER.info("{} said {}, notify webhooks", serverPlayer.getName().getString(), playerChatMessage.signedContent());
        WEBHOOKMIKU.setUsername("[" + CONFIG.serverName + "] " + serverPlayer.getName().getString());
         WEBHOOKRAD.setUsername("[" + CONFIG.serverName + "] " + serverPlayer.getName().getString());
        WEBHOOKMIKU.setAvatarUrl("https://starlightskins.lunareclipse.studio/render/default/" + serverPlayer.getName().getString() + "/face");
        WEBHOOKRAD.setAvatarUrl("https://starlightskins.lunareclipse.studio/render/default/" + serverPlayer.getName().getString() + "/face");
        WEBHOOKMIKU.setContent(playerChatMessage.signedContent());
        WEBHOOKRAD.setContent(playerChatMessage.signedContent());
        try {
            WEBHOOKMIKU.execute();
            WEBHOOKRAD.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onCommand(PlayerChatMessage playerChatMessage, CommandSourceStack commandSourceStack, ChatType.Bound bound) {}

    public void onGameMessage(MinecraftServer minecraftServer, Component component, boolean b) {
        if (component.getString().contains("has completed the challenge")) {
            //component.getString()
        } else if (!component.getString().contains("joined the game") && !component.getString().contains("left the game") /*&& !component.getString().contains("has made the advancement") && !component.getString().contains("has completed the challenge") && !component.getString().contains("has reached the goal")*/) {
            LOGGER.info("{}, notify webhooks", component.getString());
            WEBHOOKMIKU.setUsername(CONFIG.serverName);
            WEBHOOKRAD.setUsername(CONFIG.serverName);
            WEBHOOKMIKU.setAvatarUrl(CONFIG.serverIconUrl);
            WEBHOOKRAD.setAvatarUrl(CONFIG.serverIconUrl);
            WEBHOOKMIKU.setContent(component.getString());
            WEBHOOKRAD.setContent(component.getString());
            try {
                WEBHOOKMIKU.execute();
                WEBHOOKRAD.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onPlayerAdvancement(ServerPlayer serverPlayer, Advancement advancement) {
        //Therift.LOGGER.info("{} got advancement {}, notify webhooks", serverPlayer.getName().getString(), advancement.name().get().getString());
        //Therift.LOGGER.info("{}, {}, {}, {}", advancement.display().get().getIcon().getItem(), advancement.display().get().getType(), advancement.display().get().getDescription().getString(), advancement.display().get().getTitle().getString());
        //WEBHOOKMIKU.setUsername(CONFIG.serverName);
        //WEBHOOKRAD.setUsername(CONFIG.serverName);
        //WEBHOOKMIKU.setAvatarUrl(CONFIG.serverIconUrl);
        //WEBHOOKRAD.setAvatarUrl(CONFIG.serverIconUrl);
        //WEBHOOKMIKU.setContent(String.format("%s made the advancement %s", serverPlayer.getName().getString(), advancement.display().get().getTitle().getString()));
        //WEBHOOKRAD.setContent(String.format("%s made the advancement %s", serverPlayer.getName().getString(), advancement.display().get().getTitle().getString()));
        // GOAL: fcfc00 TASK: 54fc54 CHALLENGE: a800a8
        //EMBED.setAuthor(String.format("[%s] %s", CONFIG.serverName, serverPlayer.getName().getString()), null, "https://starlightskins.lunareclipse.studio/render/default/" + serverPlayer.getName().getString() + "/face");
        //switch (advancement.display().get().getType()) {
        //    case GOAL -> EMBED.setColor(new Color(5569620));
        //    case TASK -> EMBED.setColor(new Color(11010216));
        //    case CHALLENGE -> EMBED.setColor(new Color(16579584));
        //}
        //EMBED.setTitle(advancement.display().get().getTitle().getString());
        //EMBED.setDescription(advancement.display().get().getDescription().getString());

        //WEBHOOKMIKU.addEmbed(EMBED);
        //WEBHOOKRAD.addEmbed(EMBED);
        //EMBED.
        //try {
        //    WEBHOOKMIKU.execute();
        //    WEBHOOKRAD.execute();
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
    }

    public void onPlayerLeave(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        LOGGER.info("{} left the server, notify webhooks", handler.player.getName().getString());
        WEBHOOKMIKU.setUsername(CONFIG.serverName);
        WEBHOOKRAD.setUsername(CONFIG.serverName);
        WEBHOOKMIKU.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKRAD.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKMIKU.setContent(handler.player.getName().getString() + " has left the game.");
        WEBHOOKRAD.setContent(handler.player.getName().getString() + " has left the game.");
        try {
            WEBHOOKMIKU.execute();
            WEBHOOKRAD.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serverShutdown(MinecraftServer server) {
        LOGGER.info("Server {} shutting down, notify webhooks", server.getPort());
        WEBHOOKMIKU.setUsername(CONFIG.serverName);
        WEBHOOKRAD.setUsername(CONFIG.serverName);
        WEBHOOKMIKU.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKRAD.setAvatarUrl(CONFIG.serverIconUrl);
        WEBHOOKMIKU.setContent("Server has stopped.");
        WEBHOOKRAD.setContent("Server has stopped.");
        try {
            WEBHOOKMIKU.execute();
            WEBHOOKRAD.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
