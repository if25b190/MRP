DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS media CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS likes CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(256) UNIQUE NOT NULL,
  email VARCHAR(256) UNIQUE,
  password VARCHAR(512) NOT NULL,
  salt VARCHAR(512) NOT NULL,
  favorite_genre VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS media (
  id SERIAL PRIMARY KEY,
  user_id INTEGER,
  title VARCHAR(512) NOT NULL,
  description VARCHAR(1024),
  media_type VARCHAR(512) NOT NULL,
  release_year INTEGER,
  genres VARCHAR(1024),
  age_restriction INTEGER,
  CONSTRAINT fk_media_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ratings (
                                       id SERIAL PRIMARY KEY,
                                       user_id INTEGER NOT NULL,
                                       media_id INTEGER NOT NULL,
                                       stars INTEGER NOT NULL,
                                       comment VARCHAR(512),
    confirmed BOOLEAN NOT NULL DEFAULT false,
    created_at timestamp with time zone NOT NULL DEFAULT (now() at time zone 'utc'),
    CONSTRAINT fk_ratings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ratings_media FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS favorites (
  user_id INTEGER,
  media_id INTEGER,
  PRIMARY KEY(user_id, media_id),
  CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_favorite_media FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
  user_id INTEGER,
  rating_id INTEGER,
  PRIMARY KEY(user_id, rating_id),
  CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_likes_rating FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE CASCADE
);
