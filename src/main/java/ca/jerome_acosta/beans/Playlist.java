package ca.jerome_acosta.beans;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Playlist {
	private String username;
	private Long playlistId;
	private String playlistName;
	private String thumbnailUrl;
}
