package com.marv.KartoffelBot.KartoffelBot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.marv.KartoffelBot.KartoffelBot.listerns.SlashCommandListener;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.RestClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class KartoffelBotApplication {

	static ApplicationContext springContext;

	public static void main(String[] args) {
		springContext = new SpringApplicationBuilder(KartoffelBotApplication.class).build().run();

		// Login
		DiscordClientBuilder.create(System.getenv("bot.token")).build().withGateway(gatewayClient -> {
			SlashCommandListener slashCommandListener = new SlashCommandListener(springContext);

			Mono<Void> onSlashCommandMono = gatewayClient
					.on(ChatInputInteractionEvent.class, slashCommandListener::handle).then();

			return Mono.when(onSlashCommandMono);
		}).block();
	}

	@Bean
	public RestClient discordRestClient() {
		return RestClient.create(System.getenv("bot.token"));
	}
}
