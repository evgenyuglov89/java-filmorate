CREATE TABLE IF NOT EXISTS "users" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "login" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "birthday" DATE
);

CREATE TABLE IF NOT EXISTS "mpa_rating" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "description" VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "films" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    "release_date" DATE,
    "duration" INTEGER NOT NULL,
    "mpa_id" BIGINT,
    CONSTRAINT fk_mpa FOREIGN KEY ("mpa_id") REFERENCES "mpa_rating"("id")
);

CREATE TABLE IF NOT EXISTS "genres" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "film_genres" (
    "film_id" BIGINT,
    "genre_id" BIGINT,
    CONSTRAINT fk_film_genres_film FOREIGN KEY ("film_id") REFERENCES "films"("id"),
    CONSTRAINT fk_film_genres_genre FOREIGN KEY ("genre_id") REFERENCES "genres"("id")
);

CREATE TABLE IF NOT EXISTS "likes" (
    "film_id" BIGINT,
    "user_id" BIGINT,
    CONSTRAINT fk_likes_film FOREIGN KEY ("film_id") REFERENCES "films"("id"),
    CONSTRAINT fk_likes_user FOREIGN KEY ("user_id") REFERENCES "users"("id")
);

CREATE TABLE IF NOT EXISTS "friendship" (
    "user_id" BIGINT,
    "friend_id" BIGINT,
    "status" BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_friendship_user FOREIGN KEY ("user_id") REFERENCES "users"("id"),
    CONSTRAINT fk_friendship_friend FOREIGN KEY ("friend_id") REFERENCES "users"("id")
);
