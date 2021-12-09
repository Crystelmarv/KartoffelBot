package com.marv.KartoffelBot.KartoffelBot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

@AllArgsConstructor
public class TrackInfo {

	@Getter
	private ArtistSimplified[] artist;
	@Getter
	private String name;
}
