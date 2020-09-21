-- INSERT INTO users (user_id, password, first_name, last_name)
-- VALUES ('mmuster', 'pass1234', 'Maxime', 'Muster'),
--     ('eschuler', 'pass1234', 'Elena', 'Schuler');

INSERT INTO songs (title, artist, label, released)
VALUES ('MacArthur Park', 'Richard Harris', 'Dunhill Records', 1968),
    ('Afternoon Delight', 'Starland Vocal Band', 'Windsong', 1976),
    ('Muskrat Love', 'Captain and Tennille', 'A&M', 1976),
    ('Sussudio', 'Phil Collins', 'Virgin', 1985),
    ('We Built This City', 'Starship', 'Grunt/RCA', 1985),
    ('Achy Breaky Heart', 'Billy Ray Cyrus', 'PolyGram Mercury', 1992),
    ('What’s Up?', '4 Non Blondes', 'Interscope', 1993),
    ('Who Let the Dogs Out?', 'Baha Men', 'S-Curve', 2000),
    ('My Humps', 'Black Eyed Peas', 'Universal Music', 2005),
    ('Chinese Food', 'Alison Gold', 'PMW Live', 2013);

INSERT INTO playlists (owner_id, name, private)
VALUES ('eschuler', 'SPublicList', false),
    ('eschuler', 'SPrivateList', true),
    ('mmuster', 'MPrivateList', true),
    ('mmuster', 'MPublicList', false)

INSERT INTO playlists_songs(playlist_id, song_id)
VALUES (1, 1),
    (1, 4),
    (1, 6),
    (1, 5),
    (2, 1),
    (2, 4),
    (3, 4),
    (3, 8),
    (4, 2),
    (4, 3),
    (4, 9)
