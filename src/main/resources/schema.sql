CREATE TABLE IF NOT EXISTS "users" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "login" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255) NOT NULL,
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

CREATE TABLE IF NOT EXISTS "directors" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "status" BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS "film_directors" (
    "film_id" BIGINT NOT NULL,
    "director_id" BIGINT NOT NULL,

    CONSTRAINT fk_film_directors_film FOREIGN KEY ("film_id") REFERENCES "films"("id"),
    CONSTRAINT fk_film_directors_director FOREIGN KEY ("director_id") REFERENCES "directors"("id")
);

CREATE TABLE IF NOT EXISTS "events" (
    "id" BIGSERIAL PRIMARY KEY,
    "user_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "type" VARCHAR(50) NOT NULL,
    "operation" VARCHAR(50) NOT NULL,

    CONSTRAINT fk_events_user FOREIGN KEY ("user_id") REFERENCES "users"("id")
);

CREATE TABLE IF NOT EXISTS "reviews" (
    "id" BIGSERIAL PRIMARY KEY,
    "user_id" BIGINT NOT NULL,
    "film_id" BIGINT NOT NULL,
    "content" TEXT NOT NULL,
    "is_positive" BOOLEAN NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_user FOREIGN KEY ("user_id") REFERENCES "users"("id"),
    CONSTRAINT fk_review_film FOREIGN KEY ("film_id") REFERENCES "films"("id")
);

CREATE TABLE IF NOT EXISTS "review_reactions" (
    "review_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "useful" BIGINT NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_reactions_review FOREIGN KEY ("review_id") REFERENCES "reviews"("id"),
    CONSTRAINT fk_review_reactions_user FOREIGN KEY ("user_id") REFERENCES "users"("id")
);