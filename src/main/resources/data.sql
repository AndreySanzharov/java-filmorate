-- Очистка и сброс идентификаторов в таблицах
TRUNCATE TABLE FRIENDS, FILMS_GENRES, GENRES, FILMS_LIKES, FILMS, MPA_RATINGS, USERS RESTART IDENTITY;

-- Включение ссылочной целостности
SET REFERENTIAL_INTEGRITY TRUE;

-- Добавление данных в таблицу GENRES
INSERT INTO GENRES (GENRE_NAME) VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

-- Добавление данных в таблицу MPA_RATINGS
INSERT INTO MPA_RATINGS (MPA_NAME) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');
