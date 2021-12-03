package com.marv.KartoffelBot.KartoffelBot.listerns;

import java.util.HashMap;
import java.util.Map;

import com.marv.KartoffelBot.KartoffelBot.KartoffelBotApplication;
import com.marv.KartoffelBot.KartoffelBot.LavaPlayerAudioProvider;
import com.marv.KartoffelBot.KartoffelBot.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public class MessageListener {

	boolean musicQuizStarted = false;
	TrackScheduler scheduler;
	boolean correct1 = false;
	boolean correct0 = false;
	
	Snowflake guildId;

	HashMap<Snowflake, Integer> scoreBoard = new HashMap<Snowflake, Integer>();

	public Mono<Void> handle(ChatInputInteractionEvent event) {
		return null;
	}

	public MessageListener(ChatInputInteractionEvent ev) {

		guildId = ev.getInteraction().getGuildId().get();
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
				} else if (musicQuizStarted == true) {
					String[] correctAnswer = formatRight(scheduler.getCurrentSong().getInfo().title);
					
					if (content.equals("?skip")) {
						
						event.getMessage().getChannel()
						.subscribe(che -> che.createMessage("SKIPPE DEN SONG!").subscribe());
						
						event.getMessage().getChannel()
						.subscribe(che -> che.createMessage("Das war: " + correctAnswer[0] + " - " + correctAnswer[1]).subscribe());
						
						resetCorrectVars();
						scheduler.nextSongInSongList();
					}
					else if(content.equals("?score"))
					{
						event.getMessage().getChannel()
						.subscribe(che -> che.createMessage("Scoreboard:" + System.lineSeparator() + getScoreboardAsString()).subscribe());
					}

					// Quiz Guess
					
//					System.out.println(content.toLowerCase() + "  " + correctAnswer[0].toLowerCase());
//					System.out.println(correct0);

					String inputString = content.toString().replaceAll("\\P{Print}", "");
					String compareString = correctAnswer[0].toString().replaceAll("\\P{Print}", "");
					String compareString2 = correctAnswer[1].toString().replaceAll("\\P{Print}", "");

					inputString = inputString.trim();
					compareString = compareString.trim();
					compareString2 = compareString2.trim();

					if (inputString.equalsIgnoreCase(compareString) && correct0 == false) {
						correct0 = true;
						System.out.println("KORRECT");
						addPointToScorboad(event.getMember().get().getId());
						event.getMessage().getChannel()
								.subscribe(che -> che.createMessage(correctAnswer[0] + " IST RICHTIG").subscribe());
					} else if (inputString.equalsIgnoreCase(compareString2) && correct1 == false) {
						correct1 = true;
						System.out.println("KORRECT");
						addPointToScorboad(event.getMember().get().getId());
						event.getMessage().getChannel()
								.subscribe(che -> che.createMessage(correctAnswer[1] + " IST RICHTIG").subscribe());
					}
					
					if(correct0 && correct1)
					{
						event.getMessage().getChannel()
						.subscribe(che -> che.createMessage("DAS WAR: " + correctAnswer[0] + " - " + correctAnswer[1]).subscribe());
						resetCorrectVars();
						scheduler.nextSongInSongList();
					}

				}

			}

		});
	}

	private String getScoreboardAsString()
	{
		String returnString = "";
		for(Snowflake snowflake : scoreBoard.keySet()) {
			Mono<Member> member = KartoffelBotApplication.ga.getMemberById(guildId, snowflake);
			var wrapper = new Object(){ String displayName = ""; };
			member.subscribe(s->{
			  wrapper.displayName = s.getDisplayName();
			});
			returnString = returnString +  wrapper.displayName + ": " + scoreBoard.get(snowflake) + System.lineSeparator();
	
		}
		return returnString;
	}
	
	private void addPointToScorboad(Snowflake snowflake) {
		if (scoreBoard.containsKey(snowflake)) {
			int i = scoreBoard.get(snowflake);
			i++;
			scoreBoard.put(snowflake, i);
		} else {
			scoreBoard.put(snowflake, 1);
		}
	}

	private void resetCorrectVars() {
		correct0 = false;
		correct1 = false;
	}

	private String[] formatRight(String title) {
		String teil = title;
		String[] formated = new String[2];
		int i = teil.indexOf("(");
		if (i > 0) {
			teil = teil.substring(0, teil.indexOf("("));
		}
		i = teil.indexOf("[");
		if (i > 0) {
			teil = teil.substring(0, teil.indexOf("["));
		}
		teil = teil.replace("\"", "");
		teil = teil.replace("'", "");
		if (teil.contains("M/V")) {
			teil = teil.replace("M/V", "");
		}
		if (teil.contains("Lyrics")) {
			teil = teil.replace("Lyrics", "");
		}

		String[] teile = teil.split("-");

		for (int j = 0; j < 2; j++) {
			if (teile.length == 1) {
				String[] teile2 = teil.split("–");
				if (teile2[j].charAt(0) == ' ') {
					teile2[j] = teile2[j].replaceFirst(" ", "");
				}
				formated[j] = teile2[j];
			} else {
				if (teile[j].charAt(0) == ' ') {
					teile[j] = teile[j].replaceFirst(" ", "");
				}
				formated[j] = teile[j];
			}

		}
		return formated;
	}
}
