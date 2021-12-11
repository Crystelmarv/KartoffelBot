package com.marv.KartoffelBot.KartoffelBot;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.marv.KartoffelBot.KartoffelBot.listerns.SlashCommandListener;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.gateway.GatewayClient;
import discord4j.rest.RestClient;
import discord4j.voice.AudioProvider;
import lombok.Setter;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class KartoffelBotApplication {

	static ApplicationContext springContext;
	
	public static GatewayDiscordClient ga;

	public static void main(String[] args) throws GoogleJsonResponseException, GeneralSecurityException, IOException {
		springContext = new SpringApplicationBuilder(KartoffelBotApplication.class).build().run();
		
		//Youtbe Test
		Youtube d = new Youtube();
		// Login
		DiscordClientBuilder.create(System.getenv("bot.token")).build().withGateway(gatewayClient -> {
			SlashCommandListener slashCommandListener = new SlashCommandListener(springContext);
			Mono<Void> onSlashCommandMono = gatewayClient
					.on(ChatInputInteractionEvent.class, slashCommandListener::handle).then();
			ga = gatewayClient;
			return Mono.when(onSlashCommandMono);
		}).block();

	}

	@Bean
	public RestClient discordRestClient() {
		return RestClient.create(System.getenv("bot.token"));
	}
}
