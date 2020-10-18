CREATE USER xogame WITH PASSWORD '123321';
CREATE DATABASE xogame;
GRANT ALL PRIVILEGES ON DATABASE xogame TO xogame;


CREATE TABLE fields(
	id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	size int NOT NULL,
	figures varchar(1)[][] NOT NULL
);

CREATE TABLE players (
	id uuid PRIMARY KEY,
	name varchar(255),
);

CREATE TABLE games (
	id uuid PRIMARY KEY,
	type varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	turn varchar(1) NOT NULL,
	winner varchar(1),
	field_id int NOT NULL,
	FOREIGN KEY (field_id) REFERENCES fields(id) ON DELETE CASCADE,
);

CREATE TABLE player_figures (
    id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    player_id uuid REFERENCES players(id),
    figure varchar(1),
    game_id uuid REFERENCES games(id) NOT NULL
);