-- таблица для рейтингов MPA
CREATE TABLE IF NOT EXISTS MPA_RATINGS (
    MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    MPA_NAME VARCHAR NOT NULL
);

--таблица для жанров
CREATE TABLE IF NOT EXISTS GENRES (
    GENRE_ID INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    GENRE_NAME VARCHAR (100) NOT NULL
);

-- таблица пользователей
CREATE TABLE IF NOT EXISTS USERS (
    USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    EMAIL VARCHAR (255) NOT NULL,
    LOGIN VARCHAR (255) UNIQUE NOT NULL,
    USERNAME VARCHAR (255),
    BIRTHDAY DATE NOT NULL
);

-- таблица фильмов с внешним ключом для рейтингов MPA
CREATE TABLE IF NOT EXISTS FILMS (
    FILM_ID INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    FILM_NAME VARCHAR (255) NOT NULL,
    DESCRIPTION VARCHAR (200) NOT NULL,
    RELEASE_DATE DATE NOT NULL,
    DURATION INTEGER NOT NULL,
    MPA_ID INTEGER NOT NULL
        REFERENCES MPA_RATINGS (MPA_ID) ON DELETE CASCADE
);

-- Таблица для лайков фильмов с составным первичным ключом
CREATE TABLE IF NOT EXISTS FILMS_LIKES (
    FILM_ID INTEGER NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID INTEGER NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILM_ID, USER_ID)
);

-- Связующая таблица для фильмов и жанров с составным первичным ключом
CREATE TABLE IF NOT EXISTS FILMS_GENRES (
    FILM_ID INTEGER NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    GENRE_ID INTEGER NOT NULL REFERENCES GENRES (GENRE_ID) ON DELETE CASCADE,
    PRIMARY KEY (FILM_ID, GENRE_ID)
);

-- Таблица для дружбы пользователей с составным первичным ключом
CREATE TABLE IF NOT EXISTS FRIENDS (
    USER_ID INTEGER NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    FRIEND_ID INTEGER NOT NULL REFERENCES USERS (USER_ID) ON DELETE CASCADE,
    PRIMARY KEY (USER_ID, FRIEND_ID)
);
