package ca.jerome_acosta;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.jerome_acosta.beans.Playlist;
import ca.jerome_acosta.databases.DatabaseAccess;



@SpringBootTest
class DatabaseTests {
	
	private DatabaseAccess db;

	@Autowired
	public void setDa(DatabaseAccess db) {
		this.db = db;
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("--DATABASE TESTS COMPLETE--");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void createPlaylist() {
		// Get original size
		int origSize = db.getAllPlaylists().size();
		
		// Create new playlist
		Playlist newPlaylist = new Playlist();
		newPlaylist.setUsername("jerome");
		newPlaylist.setThumbnailUrl("https://www.youtube.com/watch?v=xzuiDavasYs");
		newPlaylist.setPlaylistName("My Playlist");
		
		// Add new playlist to database.
		db.createPlaylist(newPlaylist);
		
		int newSize = db.getAllPlaylists().size();
		
		// newSize should be origSize + 1 because 1 playlist was added.
		assertEquals(newSize, origSize + 1);
	}
	
	@Test
	void deletePlaylist() {
		
		// Playlist to Delete, contains 3 playlist songs
		long playlistId = 1;
		
		// Get size of all Playlists
		int origPlaylistsSize = db.getAllPlaylists().size();
		// Get number of songs associated with the specific playlist
		int origPlaylistSongsSize = db.getPlaylistSongs(playlistId).size();
		
		// Delete a playlist
		int result = db.deletePlaylist(playlistId);
		
		int newPlaylistsSize = db.getAllPlaylists().size(); 		
		int newPlaylistSongsSize = db.getPlaylistSongs(playlistId).size(); 
		
		// newPlaylistsSize should be origSize - 1 since deleted 1 playlist.
		assertEquals(newPlaylistsSize, origPlaylistsSize - 1);
		// origPlaylistsSongsSize should be == 3 because there are songs in the playlist.
		assertTrue(origPlaylistSongsSize == 3);
		// newPlaylistSongSize should return 0 since the playlist no longer exists
		assertTrue(newPlaylistSongsSize == 0);
	}


}
