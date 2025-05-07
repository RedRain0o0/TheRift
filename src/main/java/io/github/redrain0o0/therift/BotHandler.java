package io.github.redrain0o0.therift;

//import discord4j.core.DiscordClientBuilder;
//import discord4j.core.GatewayDiscordClient;
//import discord4j.core.event.domain.lifecycle.ReadyEvent;
//import discord4j.core.object.entity.User;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class BotHandler extends ListenerAdapter implements EventListener {

    private static final Config config = Therift.CONFIG;

    public static void create(Config config) throws InterruptedException {

        JDA jda = JDABuilder.createDefault(config.token)
                .addEventListeners(new BotHandler())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        /*GatewayDiscordClient client = DiscordClientBuilder.create("MTMxNzIyNjAwNDI1ODg4NTcwMw.GczdUK.V6SNZHMhgd4646_IokPm3-BMwZWe7TYCvbNd3Y").build().login().;
        if (client != null) {
            client.getEventDispatcher().on(ReadyEvent.class)
                    .subscribe(event -> {
                        User self = event.getSelf();
                        Therift.LOGGER.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
                    });
            client.onDisconnect().block();
        }*/
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.PRIVATE) && !event.getAuthor().isBot() && (event.getChannel().getId().matches(config.channelIdRad) || event.getChannel().getId().matches(config.channelIdMiku))) {
            Therift.LOGGER.info("[{}] {}: {}", event.getGuild().getName(), Objects.requireNonNull(event.getMember()).getEffectiveName(), event.getMessage().getContentDisplay());
            for (String playerName : Therift.rootserver.getPlayerNames()) {
                Player player = Therift.rootserver.getPlayerList().getPlayerByName(playerName);
                player.displayClientMessage(
                        Component.empty().append(
                                Component.empty().append("[").append(event.getGuild().getName()).append("] ")
                        ).append(
                                Component.empty().append(Objects.requireNonNull(event.getMember()).getEffectiveName()).withColor(event.getMember().getColorRaw())
                        )
                                .append(": ").append(event.getMessage().getContentDisplay()
                        ), false);
            }
        }
    }

    //@Override
    //public void onEvent(GenericEvent event) {
    //    if (event instanceof ReadyEvent)
    //        System.out.println("API is ready!");
    //}
}