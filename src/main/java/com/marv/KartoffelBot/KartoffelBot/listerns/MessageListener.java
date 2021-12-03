package com.marv.KartoffelBot.KartoffelBot.listerns;

import java.util.Arrays;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.marv.KartoffelBot.KartoffelBot.LavaPlayerAudioProvider;
import com.marv.KartoffelBot.KartoffelBot.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Mono;

public class MessageListener {
	
	boolean musicQuizStarted = false;
	TrackScheduler scheduler;

	public Mono<Void> handle(ChatInputInteractionEvent event) {
		return null;
	}

	public MessageListener(ChatInputInteractionEvent ev) {

		// LavaPlayer
		// Creates AudioPlayer instances and translates URLs to AudioTrack instances
		final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

		// This is an optimization strategy that Discord4J can utilize.
		// It is not important to understand
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

		// Allow playerManager to parse remote sources like YouTube links
		AudioSourceManagers.registerRemoteSources(playerManager);

		// Create an AudioPlayer so Discord4J can receive audio data
		final AudioPlayer player = playerManager.createPlayer();

		// We will be creating LavaPlayerAudioProvider in the next step
		AudioProvider provider = new LavaPlayerAudioProvider(player);

		ev.getClient().getEventDispatcher().on(MessageCreateEvent.class).subscribe(event -> {
			if (event.getMessage().getChannelId().equals(ev.getInteraction().getChannelId())) {

				final String content = event.getMessage().getContent();
				System.out.println(content);
				if (musicQuizStarted == false && content.contains("https")) {
					System.out.println("DD");
					final Member member = event.getMember().orElse(null);
					if (member != null) {
						final VoiceState voiceState = member.getVoiceState().block();
						if (voiceState != null) {
							final VoiceChannel channel = voiceState.getChannel().block();
							if (channel != null) {
								// join returns a VoiceConnection which would be required if we were
								// adding disconnection features, but for now we are just ignoring it.
								channel.join(spec -> spec.setProvider(provider)).block();
							}
						}
					}
					scheduler = new TrackScheduler(player);
					
					playerManager.loadItem(content, scheduler);
					musicQuizStarted = true;
				}
				else
				{
					if(content.equals("?skip"))
					{
						scheduler.nextSongInSongList();
					}
					
					//Quiz Guess
					
				}

			}

		});
	}
}
