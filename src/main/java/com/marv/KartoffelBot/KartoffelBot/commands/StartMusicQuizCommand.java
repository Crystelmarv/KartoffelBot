package com.marv.KartoffelBot.KartoffelBot.commands;

import org.springframework.stereotype.Component;

import com.marv.KartoffelBot.KartoffelBot.listerns.MessageListener;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

@Component
public class StartMusicQuizCommand implements SlashCommand {

	MessageListener mes;

	@Override
	public String getName() {
		return "start-music-quiz";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		mes = new MessageListener(event);
		return event.reply().withEphemeral(true).withContent("Start Music Quiz");
	}

	private Mono<Void> ChangeChanelName(MessageCreateEvent event) {
		// channel.edit().withName("TEST").subscribe(d -> System.out.println(d));
		System.out.println(event.getMessage().getContent());
		return null;

	}

}
