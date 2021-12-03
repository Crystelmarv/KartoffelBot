package com.marv.KartoffelBot.KartoffelBot;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public final class TrackScheduler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private List<AudioTrack> songList;
    private AudioTrack currentSong;
    

    public TrackScheduler(final AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        // LavaPlayer found an audio source for us to play
        player.playTrack(track);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
    	// LavaPlayer found multiple AudioTracks from some playlist
    	System.out.println("PLAYLIST FOUND");
    	songList = playlist.getTracks();
    	nextSongInSongList();
    	
    	
        
    }

    @Override
    public void noMatches() {
        // LavaPlayer did not find any audio to extract
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        // LavaPlayer could not parse an audio source for some reason
    }
    
    public void nextSongInSongList()
    {
    	if(currentSong != null)
    	{
    		songList.remove(currentSong);
    	}
    	int randomNum = ThreadLocalRandom.current().nextInt(1, songList.size() + 1);
    	player.playTrack(songList.get(randomNum));
    	System.out.println(songList.get(randomNum).getInfo().title);
    	currentSong = songList.get(randomNum);
    }
}
