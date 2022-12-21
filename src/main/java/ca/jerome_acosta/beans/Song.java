package ca.jerome_acosta.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Song {
	private Long songId;
	private String songUrl;
	private String artistName;
	private String songName;
}
