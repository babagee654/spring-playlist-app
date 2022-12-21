--
CREATE TABLE users(
	username VARCHAR(100) NOT NULL PRIMARY KEY,
	password VARCHAR(100) NOT NULL,
	enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
	username VARCHAR(100) NOT NULL,
	authority VARCHAR(100) NOT NULL,
	CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);
CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);

CREATE TABLE playlists (
	playlistId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(100) not null REFERENCES users(username),
	playlistName VARCHAR(100),
	thumbnailUrl VARCHAR(10000)
);

CREATE TABLE songs (
	songId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	songName VARCHAR(100),
	artistName VARCHAR(100),
	songUrl VARCHAR(200)
);

CREATE TABLE playlistsSongs (
	playlistSongId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	playlistId INT NOT NULL REFERENCES playlists(playlistId),
	songId INT NOT NULL REFERENCES songs(songId)
);