
INSERT INTO users (username, password, enabled)
VALUES
	('jerome', '$2a$04$BrrnaK9wk8eangS1L/wrMuB/kusMVSF3XBlErzsz7f25Nr3cs21p2', TRUE),
	('porp', '$2a$04$pSLSw3TxG6tDCFA/8lP98uzrZ.aTT64YOdsVlzLjLVPj2GcyfV4Fe', TRUE);
	
INSERT into authorities (username, authority)
VALUES
	('jerome', 'ROLE_USER'),
	('porp', 'ROLE_USER'),
	('porp', 'ROLE_ADMIN');
	
INSERT INTO playlists 
	(
		username,
		playlistName,
		thumbnailUrl
	)
VALUES 
	(
		'jerome', 
		'JerGnoming Around', 
		'https://i.kym-cdn.com/photos/images/newsfeed/000/813/182/1cc.png'
	),
	(
		'porp', 
		'Roomba Zumba Zounds', 
		'https://i.kym-cdn.com/photos/images/newsfeed/000/374/798/ac4.jpg'
	),
	(
		'jerome', 
		'Fly Fly Fly Fly Fly Fly', 
		'https://www.thespruce.com/thmb/_cbRf4yqHfVySGz9KZY-7DulFqQ=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/how-to-get-rid-of-flies-1389068-03-c1bd8b5d723844a993f35f0ac46b16b3.jpg'
	);
	
INSERT INTO songs (
		songName,
		artistName,
		songUrl
	)
VALUES 
	(
		'Runaway',
		'Ye',
		'https://www.youtube.com/watch?v=Bm5iA4Zupek'
	),
	(
		'Poland',
		'Lil Yachty',
		'https://www.youtube.com/watch?v=s9PzYuVwCSE'
	),
	(
		'Spooky Scary Skeletons',
		'Andrew Gold',
		'https://www.youtube.com/watch?v=sVjk5nrb_lI'
	),
	(
		'Baby Shark',
		'Cocomelon',
		'https://www.youtube.com/watch?v=020g-0hhCAU'
	),
	(
		'Cooking by the Book remix ft. Lil Jon',
		'Lazy Town',
		'https://www.youtube.com/watch?v=K5tVbVu9Mkg'
	),
	(
		'The Creator Has A Plan',
		'Pharoah Sanders',
		'https://www.youtube.com/watch?v=lhDcb9YaliM'
	),
	(
		'War With Heaven',
		'Keshi',
		'https://www.youtube.com/watch?v=wBVgy71BWes'
	),
	(
		'Multi-Love',
		'Unknown Mortal Orchestra',
		'https://www.youtube.com/watch?v=bEtDVy55shI'
	);
	
INSERT INTO playlistsSongs (
		playlistId,
		songId
	)
VALUES
	(
		1,
		1
	),
	(
		1,
		2
	),
	(
		1,
		3
	),
	(
		2,
		4
	),
	(
		2,
		5
	),
	(
		3,
		6
	),
	(
		3,
		7
	),
	(
		3,
		8
	);

