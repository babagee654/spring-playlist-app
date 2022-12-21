package ca.jerome_acosta;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ca.jerome_acosta.beans.Playlist;
import ca.jerome_acosta.beans.Song;
import ca.jerome_acosta.databases.DatabaseAccess;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTests {
	
	private DatabaseAccess db;
	private MockMvc mockMvc;
	
	@Autowired
	public void setDa(DatabaseAccess db) {
		this.db = db;
	}
	@Autowired
	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("--CONTROLLER TESTS COMPLETE--");
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}


	@Test
	@WithMockUser(username="testUser", roles={"USER"})
	void testNotAuthorizedAddSong() throws Exception {
		
		String urlString = String.format("/admin/addSong");
		
		Song newSong = new Song();
		newSong.setArtistName("2 Chainz");
		newSong.setSongName("It's a Vibe");
		newSong.setSongUrl("https://www.youtube.com/watch?v=tU3p6mz-uxU");
		
		int sizeBefore = db.getAllSongs().size();
		
		// Only users with ADMIN role should be able to add Songs
		mockMvc.perform(post(urlString).flashAttr("newSong", newSong))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl("/permission-denied"));
		
		int sizeAfter = db.getAllSongs().size();
		
		// Size of list of Songs should remain the same.
		assertEquals(sizeAfter, sizeBefore);		
	}
	
	@Test
	@WithMockUser(username="testUser", roles={"USER"})
	void testAuthenticatedEditPlaylist() throws Exception {
		
		String urlString = String.format("/user/editPlaylist");
		
		// Original Playlist
		long playlistId = 1;
		Playlist originalPlaylist = db.getPlaylist(playlistId);
		
		// Create Playlist to upload to database
		Playlist uploadPlaylist = new Playlist();
		
		// Maintain same Username and PlaylistId as original
		uploadPlaylist.setPlaylistId(originalPlaylist.getPlaylistId());
		uploadPlaylist.setUsername(originalPlaylist.getUsername()); 
		// Update playlist name and thumbnail URL
		String newPlaylistName = "NEW NAME";
		String newThumbnailUrl = "NEW URL";
		uploadPlaylist.setPlaylistName(newPlaylistName);
		uploadPlaylist.setThumbnailUrl(newThumbnailUrl);

		// After post request, user should be redirected to Original Playlist's owner page.
		String redirectUrlString = String.format("/user/%s", originalPlaylist.getUsername());
		
		mockMvc.perform(post(urlString)
				.flashAttr("playlist", uploadPlaylist) // New Playlist uploaded to database
				.param("addSong", "none")) // No song added to Playlist
		.andExpect(status().isFound())
		.andExpect(redirectedUrl(redirectUrlString));
		
		// Retrieve updated playlist
		Playlist updatedPlaylist = db.getPlaylist(playlistId); 
		
		// updatedPlaylist should be different from original playlist
		assertNotEquals(updatedPlaylist, originalPlaylist);
		
		// updatedPlaylist Username and PlaylistId should remain the same as original
		assertEquals(updatedPlaylist.getUsername(), originalPlaylist.getUsername());
		assertEquals(updatedPlaylist.getPlaylistId(), originalPlaylist.getPlaylistId());
		
		// updatedPlaylist Name and thumbnail Url should be equal to what was uploaded
		assertEquals(updatedPlaylist.getPlaylistName(), uploadPlaylist.getPlaylistName());
		assertEquals(updatedPlaylist.getThumbnailUrl(), uploadPlaylist.getThumbnailUrl());
				
	}

}
