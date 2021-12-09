package com.marv.KartoffelBot.KartoffelBot;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;

import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public class Spotify {
	private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(System.getenv("spotify.client.id")).setClientSecret(System.getenv("spotify.client.secret"))
			.build();
	private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

	public Spotify() {
		try {
			final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

			// Set access token for further "spotifyApi" object usage
			spotifyApi.setAccessToken(clientCredentials.getAccessToken());

			System.out.println("Expires in: " + clientCredentials.getExpiresIn());
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public PlaylistTrack[] getTracksFromPlaylist() {
		try {
			GetPlaylistsItemsRequest getSomethingRequest = spotifyApi.getPlaylistsItems("37i9dQZF1DX26MMm9GTjCc")
					.market(CountryCode.DE).build();

			// TODO look in paging so realy every track could be picked
			final Paging<PlaylistTrack> some = getSomethingRequest.execute();
			System.out.println(some.getLimit());

			PlaylistTrack playListTracks[] = some.getItems();

			return playListTracks;

		} catch (Exception e) {
			System.out.println("Something went wrong!\n" + e.getMessage());
		}
		return null;
	}

	public TrackInfo getTrackInfos(PlaylistTrack playlistTrack)
			throws ParseException, SpotifyWebApiException, IOException {
		String songid = playlistTrack.getTrack().getId();

		GetTrackRequest getTrackRequst = spotifyApi.getTrack(songid).build();
		Track track = getTrackRequst.execute();
		TrackInfo trackInfo = new TrackInfo(track.getArtists(), track.getName());
		return trackInfo;
	}

}
