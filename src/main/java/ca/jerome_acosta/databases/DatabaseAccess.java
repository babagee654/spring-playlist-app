package ca.jerome_acosta.databases;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.jerome_acosta.beans.Playlist;
import ca.jerome_acosta.beans.Song;
import ca.jerome_acosta.beans.User;

@Repository
public class DatabaseAccess {
	
	private NamedParameterJdbcTemplate jdbc;
	
	public DatabaseAccess(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	
	// GENERAL DATABASE METHODS
	public User getUser(String username) {
		
		String query = "SELECT * FROM users WHERE username = :username";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		
		BeanPropertyRowMapper<User> mapper = new BeanPropertyRowMapper<>(User.class);
		User user = null;
		try {
			user = jdbc.queryForObject(query, params, mapper);
		}
		catch (Exception e) {
			System.out.println("No User with username: " + username);
		}
		
		return user;
	}
	
	public List<User> getAllUsers() {
		
		String query = "SELECT * FROM users";
		
		BeanPropertyRowMapper<User> mapper = new BeanPropertyRowMapper<>(User.class);
		
		List<User> users = jdbc.query(query, mapper);
		
		return users;
	}
	
	public Playlist getPlaylist(long playlistId) {
		String query = "SELECT * FROM playlists WHERE playlistId = :playlistId";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("playlistId", playlistId);
		
		BeanPropertyRowMapper<Playlist> mapper = new BeanPropertyRowMapper<>(Playlist.class);
		
		Playlist playlist = jdbc.queryForObject(query, params, mapper);
		
		return playlist;
	}
	
	public List<Playlist> getAllPlaylists() {
		String query = "SELECT * from playlists";
		
		BeanPropertyRowMapper<Playlist> mapper = new BeanPropertyRowMapper<>(Playlist.class);
		
		List<Playlist> playlists = jdbc.query(query, mapper);
		
		return playlists;
	}
	
	public List<Playlist> getUserPlaylists(String username) {
		String query = "SELECT * FROM playlists WHERE username = :username";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		
		BeanPropertyRowMapper<Playlist> mapper = new BeanPropertyRowMapper<>(Playlist.class);
		
		List<Playlist> playlists = null;
							
		try {
			playlists = jdbc.query(query, params, mapper);
		}
		catch (Exception e) {
			System.out.println("No User with username: " + username);
		}
		
		return playlists;
	}
	
	public Song getSong(long songId) {
		String query = "SELECT * FROM songs WHERE songId = :songId";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("songId", songId);
		
		BeanPropertyRowMapper<Song> mapper = new BeanPropertyRowMapper<>(Song.class);
		
		Song song = jdbc.queryForObject(query, params, mapper);
		
		return song;
	}
	
	public List<Song> getAllSongs(){
		String query = "SELECT * FROM songs";
		
		BeanPropertyRowMapper<Song> mapper = new BeanPropertyRowMapper<>(Song.class);
		
		List<Song> songs = jdbc.query(query, mapper);
		
		return songs;
	}
	
	public List<Song> getPlaylistSongs(long playlistId) {
		String query = "SELECT * FROM songs S"
				+ " INNER JOIN playlistsSongs P"
				+ " ON playlistId = :playlistId"
				+ " AND S.songId = P.songId";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("playlistId", playlistId);
		
		BeanPropertyRowMapper<Song> mapper = new BeanPropertyRowMapper<>(Song.class);
		
		List<Song> songs = jdbc.query(query, params, mapper);
		
		return songs;		
	}
	
	
	// USER ABILITIES
	public int updatePlaylist(Playlist updatedPlaylist) {
		String query = "UPDATE playlists SET playlistName = :playlistName, thumbnailUrl = :thumbnailUrl WHERE playlistId = :playlistId";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("playlistId", updatedPlaylist.getPlaylistId())
			.addValue("playlistName", updatedPlaylist.getPlaylistName())
			.addValue("thumbnailUrl", updatedPlaylist.getThumbnailUrl());
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}
	
	public int addSongToPlaylist(Long playlistId, Long songId) {
		String query = "INSERT INTO playlistsSongs (playlistId, songId) VALUES (:playlistId, :songId)";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("playlistId", playlistId)
			.addValue("songId", songId);
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}
	
	public int deleteSongFromPlaylist(Long playlistId, Long songId) {
			
		String query = "DELETE FROM playlistssongs WHERE playlistId = :playlistId AND songId = :songId";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("playlistId", playlistId)
		.addValue("songId", songId);
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}
	
	public int createPlaylist(Playlist createdPlaylist) {
		String query = "INSERT INTO playlists (username, playlistName, thumbnailUrl)"
				+ " VALUES (:username, :playlistName, :thumbnailUrl)";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", createdPlaylist.getUsername())
			.addValue("playlistName", createdPlaylist.getPlaylistName())
			.addValue("thumbnailUrl", createdPlaylist.getThumbnailUrl());
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}

	
	// ADMIN ABILITIES
	public int deletePlaylist(Long playlistId) {
		
		// Delete playlistsSongs connected to the playlist first, then delete the playlist itself.
		String query = "DELETE FROM playlistsSongs WHERE playlistId = :playlistId; "
				+ "DELETE FROM playlists WHERE playlistId = :playlistId;";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("playlistId", playlistId);
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}
	
	public int addSongToDatabase(Song newSong) {
		String query = "INSERT INTO songs (songName, artistName, songUrl)"
				+ " VALUES (:songName, :artistName, :songUrl)";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("songName", newSong.getSongName())
			.addValue("artistName", newSong.getArtistName())
			.addValue("songUrl", newSong.getSongUrl());
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}
	
	public int deleteUser(String username) {
		
		// Get all Playlists by user
		List<Playlist> userPlaylists = this.getUserPlaylists(username);
		
		// Delete all Playlists by user 
		for (Playlist p : userPlaylists) {
			this.deletePlaylist(p.getPlaylistId());
		}
		
		// Delete User from Authorities and Users table
		String query = "DELETE FROM authorities WHERE username = :username; "
				+ "DELETE FROM users WHERE username = :username;";
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		params.addValue("username", username);
		
		int queryResult = jdbc.update(query, params);
		
		return queryResult;
	}
	
}
