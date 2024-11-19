![img.png](img.png)
```
Table films {
film_id integer [primary key]
film_name varchar
description varchar
release_date date
duration integer
mpa_id int
}

Table users {
user_id integer [primary key]
email varchar
login varchar
name varchar
birthday date
}

Table genres {
genre_id integer [primary key]
name varchar
}

Table films_genres {
genre_id integer
film_id integer
}

Table films_likes {
film_id integer
user_id integer
}

Table friends{
user_id integer
friend_id integer
}

Table mpa_ratings {
mpa_id integer
mpa_name varhcar
}

ref: genres.genre_id < films_genres.genre_id
ref: films.film_id < films_genres.film_id
ref: films.film_id < films_likes.film_id
ref: films.mpa_id < mpa_ratings.mpa_id
ref: users.user_id < films_likes.user_id
ref: users.user_id < friends.user_id
ref: users.user_id < friends.friend_id
```

https://dbdiagram.io/d/Filmorate-671faa2d97a66db9a3882f93
