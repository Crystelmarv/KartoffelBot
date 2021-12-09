package com.marv.KartoffelBot.KartoffelBot;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;

import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;

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

	void getSomething_Sync() {
		try {
			// Execute the request synchronous
			// Create a request object with the optional parameter "market"
			GetPlaylistRequest getSomethingRequest = spotifyApi.getPlaylist("5Uulh7HBve8RJbJPsteXsm")
					.market(CountryCode.DE).build();
			final Playlist something = getSomethingRequest.execute();

			// Print something's name
			System.out.println("Name: " + something.getName());
		} catch (Exception e) {
			System.out.println("Something went wrong!\n" + e.getMessage());
		}
	}

}
