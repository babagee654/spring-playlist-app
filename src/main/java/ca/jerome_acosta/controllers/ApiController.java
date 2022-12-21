package ca.jerome_acosta.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.jerome_acosta.beans.Message;
import ca.jerome_acosta.beans.Playlist;
import ca.jerome_acosta.beans.Song;
import ca.jerome_acosta.beans.User;
import ca.jerome_acosta.databases.DatabaseAccess;
import lombok.AllArgsConstructor;


@RestController

//Using @RequestMapping to default requests to /api
@RequestMapping("/api")
@AllArgsConstructor
public class ApiController {
	private DatabaseAccess db;
	
	// Get all Users
	@GetMapping("/users")
	public List<User> getUsers() {
		return db.getAllUsers();
	}
	
	// Get specific User
	@GetMapping("/users/{username}")
	public ResponseEntity<?> getUsers(@PathVariable String username) {
		User user = db.getUser(username);
		
		if (user != null) {
			return ResponseEntity.status(HttpStatus.OK).body(user);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Error", "Username not found."));
		}
	}
	
	// Get User's playlists
	@GetMapping("/users/{username}/playlists")
	public ResponseEntity<?> getUserPlaylists(@PathVariable String username) {
		
		List<Playlist> playlists = db.getUserPlaylists(username);
		
		if (playlists != null) {
			return ResponseEntity.status(HttpStatus.OK).body(playlists);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Error", "Username not found."));
		}
	}
	
	// Add Song to database
	@PostMapping (value= "/addSong", consumes="application/json")
	public ResponseEntity<?> addSong(@RequestBody Song song){

		List<Song> allSongs = db.getAllSongs();
		
		String newSongName = song.getSongName().toLowerCase();
		String newSongArtist = song.getArtistName().toLowerCase();
		
		// Check if already in database song name and artist are already in database.
		for (Song s : allSongs) {
			String dbSongName = s.getSongName().toLowerCase();
			String dbSongArtist = s.getArtistName().toLowerCase();
			
			if (newSongName.equals(dbSongName) && newSongArtist.equals(dbSongArtist)) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new Message("Error", "Song is already in database."));
			}
		}
		db.addSongToDatabase(song);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(song);
	}
	
	
}
