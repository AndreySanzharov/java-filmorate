CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT PRIMARY KEY,
    name VARCHAR NOT NULL,
    description TEXT,
    release_date DATE,
    duration INTEGER,
    rate INTEGER
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id BIGINT,
    friend_id BIGINT,
    status VARCHAR,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS film_ratings (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS mutual_friends (
    user_id BIGINT,
    other_user_id BIGINT,
    friend_id BIGINT,
    PRIMARY KEY (user_id, other_user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (other_user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);
