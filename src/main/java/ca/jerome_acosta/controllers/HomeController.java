package ca.jerome_acosta.controllers;

import java.util.ArrayList;

/**
 * Author: Jerome Acosta
 */

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import ca.jerome_acosta.beans.Playlist;
import ca.jerome_acosta.beans.SearchResult;
import ca.jerome_acosta.beans.Song;
import ca.jerome_acosta.beans.User;
import ca.jerome_acosta.databases.DatabaseAccess;



@Controller
public class HomeController {
	
	private DatabaseAccess db;
	private BCryptPasswordEncoder encoder;
	private JdbcUserDetailsManager manager;
	
	public HomeController(DatabaseAccess db, BCryptPasswordEncoder encoder, JdbcUserDetailsManager manager) {
		this.db = db;
		this.encoder = encoder;
		this.manager = manager;
	}
	
//	public HomeController(DatabaseAccess db,  JdbcUserDetailsManager manager) {
//		this.db = db;
//		this.manager = manager;
//	}
	
	// Method to parse youtube links
	public static String parseYoutubeLink(String youtubeLink) {
		
		
		String result="";
		// Handles https://www.youtube.com/watch?v=_______ links
		if (youtubeLink.contains("v=")) {
			int startIndex = youtubeLink.indexOf("v=") + 2;
			result = youtubeLink.substring(startIndex);
		}
		// Handles https://youtu.be/________ links
		else if(youtubeLink.contains(".be/")){
			int startIndex = youtubeLink.indexOf(".be/") + 4;
			result = youtubeLink.substring(startIndex);
		}
		else {
			result = "error unable to parse string";
		}
		return result;
	}
	
	// Login
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	// Unauthorized page
	@GetMapping("/permission-denied")
	public String goToDenied() {
		return "/error/permission-denied";
	}
	
	// Home page
	@GetMapping("/")
	public String goHome(Model model) {
		
		List<User> users = db.getAllUsers();
		
		// Print out login credentials
		
		System.out.println("Username and Password are the same");
		System.out.println("Default Users:");
		System.out.println("jerome, porp");
		
		List<Playlist> playlists = db.getAllPlaylists();
		
		// Add last added playlist to the Model
		if (playlists.size() > 0) {
			model.addAttribute("newestPlaylist", playlists.get(playlists.size() - 1));
		}
		
		// Generate index for random playlist and add to model
		int randomPlaylist =  (int) (Math.floor(Math.random() * playlists.size()) + 1);
		model.addAttribute("randomPlaylist", randomPlaylist);
		
		return "index";
	}
	
	// View all Playlists page
	@GetMapping("/viewPlaylists")
	public String viewPlaylists(Model model) {
		
		List<Playlist> playlists = db.getAllPlaylists();
		
		model.addAttribute("playlists", playlists);
		
		return "/security/any/viewPlaylists";
	}
	
	// View specific playlist page
	@GetMapping("/viewPlaylists/{playlistId}")
	public String viewSinglePlaylist(@PathVariable int playlistId, Model model) {
		
		Playlist playlist = db.getPlaylist(playlistId);
		
		List<Song> playlistSongs = db.getPlaylistSongs(playlistId);
		
		model.addAttribute("playlist", playlist);
		model.addAttribute("playlistSongs", playlistSongs);
		
		return "/security/any/viewPlaylist";
	}
	
	// Play song on a playlist
	@GetMapping("/viewPlaylists/{playlistId}/{songQueue}")
	public String watchPlaylist(@PathVariable int playlistId, @PathVariable int songQueue, Model model) {
		Playlist playlist = db.getPlaylist(playlistId);
		
		List<Song> playlistSongs = db.getPlaylistSongs(playlistId);	
		
		Song currentSong = playlistSongs.get(songQueue - 1);
		
		model.addAttribute("playlist", playlist);
		model.addAttribute("playlistSongs", playlistSongs);
		model.addAttribute("currentSong", currentSong);
		model.addAttribute("songUrl", parseYoutubeLink(currentSong.getSongUrl()));
		model.addAttribute("songQueue", songQueue);
		model.addAttribute("playlistSize", playlistSongs.size());
		
		return "/security/any/playSong";
	}
	
	// Go to User Profile
	@GetMapping("/user/{username}")
	public String viewUser(@PathVariable String username, Model model) {
		
		User user = db.getUser(username);
		
		List<Playlist> userPlaylists = db.getUserPlaylists(username);
		
		Playlist newPlaylist = new Playlist();
		
		model.addAttribute(user);
		model.addAttribute("playlists", userPlaylists);
		model.addAttribute("newPlaylist", newPlaylist);
		
		return "/security/user/userPage";
	}
	
	// Edit user playlist
	@GetMapping("/user/editPlaylist/{playlistId}")
	public String editPlaylist(@PathVariable int playlistId, Model model) {
		
		List<Song> availableSongs = db.getAllSongs();
		Playlist playlist = db.getPlaylist(playlistId);
		
		List<Song> playlistSongs = db.getPlaylistSongs(playlistId);
		
		model.addAttribute("songsList", availableSongs);
		model.addAttribute("playlist", playlist);
		model.addAttribute("playlistSongs", playlistSongs);
		
		return "/security/user/editPlaylist";
	}
	
	// Post updated playlist to database
	@PostMapping("/user/editPlaylist")
	public String submitEditedPlaylist(@ModelAttribute Playlist updatePlaylist, @RequestParam String addSong) {
		
		db.updatePlaylist(updatePlaylist);
		
		// Only add song if "none" is not selected.
		if (!addSong.equals("none")) {
			db.addSongToPlaylist(updatePlaylist.getPlaylistId(), Long.parseLong(addSong));
		}
		
		return String.format("redirect:/user/%s", updatePlaylist.getUsername());
	}
	
	// Delete songs from playlist
	@GetMapping("/user/deleteSong/{playlistId}/{songId}")
	public String deleteSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
	
		db.deleteSongFromPlaylist(playlistId, songId);
		
		return String.format("redirect:/user/editPlaylist/%d", playlistId);
	}
	
	// Create a Playlist
	@PostMapping("/user/createPlaylist")
	public String deleteSongFromPlaylist(@ModelAttribute Playlist newPlaylist) {

		
		int result = db.createPlaylist(newPlaylist);
		
		return String.format("redirect:/user/%s", newPlaylist.getUsername());
	}
	
	// Go to Admin Page
	@GetMapping("/admin")
	public String viewAdminDashboard(Model model) {
		
		List<Playlist> playlists = db.getAllPlaylists();
		List<User> users = db.getAllUsers();
		
		// Remove admin from list of users.
		for (User u : users) {
			if (u.getUsername().equals("porp")) {
				users.remove(u);
				break;
			}
		}
		
		model.addAttribute("playlists", playlists);
		model.addAttribute("users", users);
		
		return "/security/admin/adminPage";
	}
	
	// Delete a playlist
	@GetMapping("/admin/deletePlaylist/{playlistId}")
	public String deletePlaylist(@PathVariable Long playlistId) {
		
		int result = db.deletePlaylist(playlistId);
		
		return "redirect:/admin";
	}
	
	// Form to add a song to database
	@GetMapping("/admin/addSong")
	public String addSongForm(Model model) {
		Song newSong = new Song();
		
		model.addAttribute("newSong", newSong);
		
		return "/security/admin/addNewSong";
	}
	
	// Add song to the database
	@PostMapping("/admin/addSong")
	public String addSongToDatabase(@ModelAttribute Song newSong) {
		
		int result = db.addSongToDatabase(newSong);
		
		return "redirect:/admin";
	}
	
	// -------- ASSIGNMENT 4 SECTION ---------
	
	
	// Form to create new users
	@GetMapping("/sign-up")
	public String goSignUp(Model model) {
		User newUser = new User();
		
		model.addAttribute("newUser", newUser);
		
		return "sign-up";
	}
	
	// Create new user
	@PostMapping("/sign-up")
	public String goSignUp(@ModelAttribute User user) {
		
		System.out.println("New User:" + user.getUsername());
		System.out.println("Password:" + user.getPassword());
		
		List<GrantedAuthority> authList = new ArrayList<>();
		
		// Only create users with ROLE_USER through sign-up method.
		authList.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		// Encode the password with BCrypt
		String encodedPwd = encoder.encode(user.getPassword());
		
		// Not sure what each attribute is for, but required to implement UserDetails
		User newUser = new User(user.getUsername(), encodedPwd, authList);
		
		// Handle creating same username.
		try {				
			manager.createUser(newUser);
		}
		catch (Exception e) {
			System.out.println("Unable to create new User: " + e);
			return "redirect:/sign-up?errorUser";
		}

		
		return "redirect:/login?newUser";
	}
	
	@GetMapping("/admin/deleteUser/{username}")
	public String deleteUser(@PathVariable String username) {
		
		db.deleteUser(username);
		
		return "redirect:/admin";
	}
	
	@GetMapping("/admin/searchSongs")
	public String searchSongsResults(@RequestParam String searchString, Model model, RestTemplate restTemplate) throws JsonMappingException, JsonProcessingException {
		
		String query = String.format("https://www.googleapis.com/youtube/v3/search?"
				+ "key=AIzaSyDFDftm9eJAJBJk3c42PHsrh5sx7wVKqvA"
				+ "&q=%s"
				+ "&type=video"
				+ "&part=snippet"
				, searchString);
		
		// Parse JSON into string
		ResponseEntity<String> responseEntity = 
				restTemplate.getForEntity(query, String.class);
		
		String json = responseEntity.getBody();
		
		// Map JSON string into an Object.
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		
		
		// Size of search results
		int resultsSize = rootNode.get("items").size();
		
		List<SearchResult> searchResults = new ArrayList<>();
		
		// Map JSON search results into SearchResult Objects.
		for (int i = 0; i < resultsSize; i++) {
			String title = rootNode.get("items").get(i).get("snippet").get("title").asText();
			String videoId = rootNode.get("items").get(i).get("id").get("videoId").asText();
			String channelTitle = rootNode.get("items").get(i).get("snippet").get("channelTitle").asText();
			
			SearchResult result = new SearchResult(title, videoId, channelTitle);
			
			searchResults.add(result);
		}
		
		Song newSong = new Song();
		
		// Add searchResults for thymeleaf model.
		model.addAttribute("searchResults", searchResults);
		model.addAttribute("newSong", newSong);
		
		return "/security/admin/searchSong";
		
	}
	
}
