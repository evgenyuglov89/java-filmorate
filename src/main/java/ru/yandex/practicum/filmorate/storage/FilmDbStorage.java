package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("dbFilmStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private static final String GET_COUNT_FILMS_BY_ID =
            "SELECT COUNT(*) FROM \"films\" WHERE \"id\" = ?";
    private static final String GET_COUNT_MPA_BY_ID =
            "SELECT COUNT(*) FROM \"mpa_rating\" WHERE \"id\" = ?";
    private static final String GET_FILM_BY_ID = """
            SELECT f."id", f."name", f."description", f."release_date" AS "releaseDate", f."duration",
            m."id" AS "mpa_id", m."name" AS "mpa_name", m."description" AS "mpa_description"
            FROM "films" f
            LEFT JOIN "mpa_rating" m ON f."mpa_id" = m."id"
            WHERE f."id" = ?""";
    private static final String GET_LIKES_BY_FILM =
            "SELECT \"user_id\" FROM \"likes\" WHERE \"film_id\" = ?";
    private static final String GET_MPA_BY_FILM =
            "SELECT * FROM \"mpa_rating\" WHERE \"id\" = ?";
    private static final String GET_GENRES_BY_FILM_ID = """
            SELECT g."id", g."name"
            FROM "film_genres" fg
            JOIN "genres" g ON fg."genre_id" = g."id"
            WHERE fg."film_id" = ?
            ORDER BY g."id"
            """;
    private static final String GET_ALL_FILMS = """
            SELECT f."id", f."name", f."description", f."release_date" AS "releaseDate",
                f."duration",
                m."id" AS "mpa_id", m."name" AS "mpa_name", m."description" AS "mpa_description"
            FROM "films" f
            LEFT JOIN "mpa_rating" m ON f."mpa_id" = m."id"
            """;
    private static final String GET_POPULAR_FILMS = """
            SELECT
                f."id", f."name", f."description", f."release_date" AS "releaseDate",
                f."duration",
                m."id" AS "mpa_id", m."name" AS "mpa_name", m."description" AS "mpa_description",
                COUNT(l."user_id") AS "likes_count"
            FROM "films" f
            LEFT JOIN "mpa_rating" m ON f."mpa_id" = m."id"
            LEFT JOIN "likes" l ON f."id" = l."film_id"
            GROUP BY
                f."id", f."name", f."description", f."release_date", f."duration",
                m."id", m."name", m."description"
            ORDER BY "likes_count" DESC, f."id" ASC
            LIMIT ?
            """;
    private static final String INSERT_NEW_FILM =
            "INSERT INTO \"films\" (\"name\", \"description\", \"release_date\", \"duration\", \"mpa_id\") " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRES =
            "INSERT INTO \"film_genres\" (\"film_id\", \"genre_id\") VALUES (?, ?)";
    private static final String INSERT_FILM_DIRECTORS =
            "INSERT INTO \"film_directors\" (\"film_id\", \"director_id\") VALUES (?, ?)";
    private static final String INSERT_LIKE =
            "INSERT INTO \"likes\" (\"film_id\", \"user_id\") VALUES (?, ?)";
    private static final String UPDATE_FILM =
            "UPDATE \"films\" SET \"name\" = ?, \"description\" = ?, \"release_date\" = ?, " +
                    "\"duration\" = ?, \"mpa_id\" = ? WHERE \"id\" = ?";
    private static final String DELETE_LIKE =
            "DELETE FROM \"likes\" WHERE \"film_id\" = ? AND \"user_id\" = ?";
    private static final String GET_DIRECTORS_BY_FILM_ID = """
                SELECT d."id", d."name"
                FROM "film_directors" fd
                JOIN "directors" d ON fd."director_id" = d."id"
                WHERE fd."film_id" = ?
                ORDER BY d."id"
            """;
    private static final String GET_FILMS_BY_DIRECTOR_SORT_BY_YEAR = """
                SELECT f."id", f."name", f."description", f."release_date" AS "releaseDate",
                       f."duration",
                       m."id" AS "mpa_id", m."name" AS "mpa_name", m."description" AS "mpa_description"
                FROM "films" f
                JOIN "film_directors" fd ON f."id" = fd."film_id"
                LEFT JOIN "mpa_rating" m ON f."mpa_id" = m."id"
                WHERE fd."director_id" = ?
                ORDER BY f."release_date" ASC NULLS LAST, f."id" ASC
            """;

    private static final String GET_FILMS_BY_DIRECTOR_SORT_BY_LIKES = """
                SELECT f."id", f."name", f."description", f."release_date" AS "releaseDate",
                       f."duration",
                       m."id" AS "mpa_id", m."name" AS "mpa_name", m."description" AS "mpa_description",
                       COALESCE(COUNT(l."user_id"), 0) AS likes_count
                FROM "films" f
                JOIN "film_directors" fd ON f."id" = fd."film_id"
                LEFT JOIN "mpa_rating" m ON f."mpa_id" = m."id"
                LEFT JOIN "likes" l ON f."id" = l."film_id"
                WHERE fd."director_id" = ?
                GROUP BY f."id", f."name", f."description", f."release_date", f."duration",
                         m."id", m."name", m."description"
                ORDER BY likes_count DESC, f."id" ASC
            """;


    @Override
    public Film save(Film film) {
        ifMpaExistsOrThrow(film.getMpa().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_NEW_FILM, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf((film.getReleaseDate())));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Не удалось получить ID нового фильма");
        }
        film.setId(key.intValue());

        List<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {

            ifGenreExists(genres);

            Set<Integer> uniqueGenreIds = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            List<Object[]> batchArgs = uniqueGenreIds.stream()
                    .map(genreId -> new Object[]{film.getId(), genreId})
                    .toList();

            jdbc.batchUpdate(INSERT_FILM_GENRES, batchArgs);
        }

        List<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            ifDirectorExists(directors);  // Метод проверяет, что такие режиссеры существуют (тебе нужно его реализовать)

            Set<Integer> uniqueDirectorIds = directors.stream()
                    .map(Director::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            List<Object[]> batchDirectorArgs = uniqueDirectorIds.stream()
                    .map(directorId -> new Object[]{film.getId(), directorId})
                    .toList();
            jdbc.batchUpdate(INSERT_FILM_DIRECTORS, batchDirectorArgs);
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        Integer count = jdbc.queryForObject(GET_COUNT_FILMS_BY_ID, new Object[]{film.getId()}, Integer.class);

        if (count != null && count > 0) {
            jdbc.update(UPDATE_FILM,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            List<Director> directors = film.getDirectors();
            jdbc.update("DELETE FROM \"film_directors\" WHERE \"film_id\" = ?", film.getId());

            if (directors != null && !directors.isEmpty()) {
                ifDirectorExists(directors);

                Set<Integer> uniqueDirectorIds = directors.stream()
                        .map(Director::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                List<Object[]> batchDirectorArgs = uniqueDirectorIds.stream()
                        .map(directorId -> new Object[]{film.getId(), directorId})
                        .toList();
                jdbc.batchUpdate(INSERT_FILM_DIRECTORS, batchDirectorArgs);
            }
        } else {
            throw new NotFoundException("Фильм который вы пытаетесь обновить не существует " + film.getId());
        }
        return film;
    }

    @Override
    public List<Film> filmsList() {
        List<Film> filmsUpload = new ArrayList<>();

        List<Film> films = jdbc.query(GET_ALL_FILMS, mapper);

        for (Film film : films) {
            getGenresAndLikesAndDirectors(film);
            filmsUpload.add(film);
        }

        return filmsUpload;
    }

    @Override
    public Film findById(int id) {
        Film film = jdbc.query(GET_FILM_BY_ID, mapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));

        List<Genre> genres = jdbc.query(
                GET_GENRES_BY_FILM_ID,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                film.getId()
        );

        Map<Integer, Genre> uniqueGenres = genres.stream()
                .collect(Collectors.toMap(
                        Genre::getId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        film.setGenres(new ArrayList<>(uniqueGenres.values()));

        Mpa mpa = jdbc.queryForObject(
                GET_MPA_BY_FILM,
                (rs, rowNum) -> new Mpa(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                ),
                film.getMpa().getId()
        );
        film.setMpa(mpa);
        List<Director> directors = jdbc.query(
                GET_DIRECTORS_BY_FILM_ID,
                (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")),
                film.getId()
        );

        Map<Integer, Director> uniqueDirectors = directors.stream()
                .collect(Collectors.toMap(
                        Director::getId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        film.setDirectors(new ArrayList<>(uniqueDirectors.values()));
        return film;
    }

    @Override
    public void likeFilm(int filmId, int userId) {
        jdbc.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = jdbc.query(GET_POPULAR_FILMS, new Object[]{count}, mapper);

        for (Film film : films) {
            getGenresAndLikesAndDirectors(film);
        }

        return films;
    }

    private void ifMpaExistsOrThrow(int mpaId) {
        Integer count = jdbc.queryForObject(
                GET_COUNT_MPA_BY_ID,
                Integer.class,
                mpaId
        );

        if (count == null || count == 0) {
            throw new NotFoundException("MPA рейтинг с id=" + mpaId + " не найден.");
        }
    }

    private void ifGenreExists(List<Genre> genres) {
        if (genres.isEmpty()) {
            return;
        }

        List<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .distinct()
                .toList();

        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));

        String query = "SELECT \"id\" FROM \"genres\" WHERE \"id\" IN (" + inSql + ")";

        List<Integer> existingIds = jdbc.query(
                query,
                genreIds.toArray(new Object[0]),
                (rs, rowNum) -> rs.getInt("id")
        );

        if (existingIds.size() != genreIds.size()) {
            List<Integer> notFound = new ArrayList<>(genreIds);
            notFound.removeAll(existingIds);
            throw new NotFoundException("Жанры не найдены: " + notFound);
        }
    }

    private void ifDirectorExists(List<Director> directors) {
        if (directors.isEmpty()) {
            return;
        }

        List<Integer> directorIds = directors.stream()
                .map(Director::getId)
                .distinct()
                .toList();

        String inSql = String.join(",", Collections.nCopies(directorIds.size(), "?"));

        String query = "SELECT \"id\" FROM \"directors\" WHERE \"id\" IN (" + inSql + ")";

        List<Integer> existingIds = jdbc.query(
                query,
                directorIds.toArray(new Object[0]),
                (rs, rowNum) -> rs.getInt("id")
        );

        if (existingIds.size() != directorIds.size()) {
            List<Integer> notFound = new ArrayList<>(directorIds);
            notFound.removeAll(existingIds);
            throw new NotFoundException("Режиссеры не найдены: " + notFound);
        }
    }


    private void getGenresAndLikesAndDirectors(Film film) {
        List<Genre> genres = jdbc.query(
                GET_GENRES_BY_FILM_ID,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                film.getId()
        );

        Set<Integer> likes = new HashSet<>(jdbc.queryForList(
                GET_LIKES_BY_FILM,
                Integer.class,
                film.getId()
        ));
        List<Director> directors = jdbc.query(
                GET_DIRECTORS_BY_FILM_ID,
                (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name")),
                film.getId()
        );
        film.setDirectors(directors);
        film.setGenres(genres);
        film.setLikes(likes);
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        Set<Integer> userLikes = getLikedFilmIdsByUser(userId);
        Map<Integer, Integer> similarityScores = findSimilarUsers(userId, userLikes);

        return findMostSimilarUser(similarityScores)
                .map(similarUserId -> getRecommendations(userId, similarUserId))
                .orElse(Collections.emptyList());
    }

    private Set<Integer> getLikedFilmIdsByUser(int userId) {
        String sql = "SELECT \"film_id\" FROM \"likes\" WHERE \"user_id\" = ?";
        return new HashSet<>(jdbc.queryForList(sql, Integer.class, userId));
    }

    private Map<Integer, Integer> findSimilarUsers(int userId, Set<Integer> userLikes) {
        String sql = "SELECT \"user_id\", \"film_id\" FROM \"likes\" WHERE \"user_id\" != ?";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, userId);

        Map<Integer, Set<Integer>> otherUsersLikes = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Integer otherUserId = ((Number) row.get("user_id")).intValue();
            Integer filmId = ((Number) row.get("film_id")).intValue();

            otherUsersLikes.computeIfAbsent(otherUserId, k -> new HashSet<>()).add(filmId);
        }

        Map<Integer, Integer> similarityScores = new HashMap<>();

        for (Map.Entry<Integer, Set<Integer>> entry : otherUsersLikes.entrySet()) {
            Set<Integer> common = new HashSet<>(entry.getValue());
            common.retainAll(userLikes);
            similarityScores.put(entry.getKey(), common.size());
        }

        return similarityScores;
    }

    private Optional<Integer> findMostSimilarUser(Map<Integer, Integer> similarityScores) {
        return similarityScores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    private List<Film> getRecommendations(int targetUserId, int similarUserId) {
        String sql = """
        SELECT f."id", f."name", f."description", f."release_date", f."duration",
               f."mpa_id", m."name" AS mpa_name, m."description" AS mpa_description
        FROM "likes" l
        JOIN "films" f ON f."id" = l."film_id"
        JOIN "mpa_rating" m ON f."mpa_id" = m."id"
        WHERE l."user_id" = ? AND l."film_id" NOT IN (SELECT "film_id" FROM "likes" WHERE "user_id" = ?)
        """;

        return jdbc.query(sql, mapper, similarUserId, targetUserId);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "year";
        } else {
            sortBy = sortBy.trim().toLowerCase();
        }

        String query = switch (sortBy) {
            case "likes" -> GET_FILMS_BY_DIRECTOR_SORT_BY_LIKES;
            case "year" -> GET_FILMS_BY_DIRECTOR_SORT_BY_YEAR;
            default -> GET_FILMS_BY_DIRECTOR_SORT_BY_YEAR;
        };

        List<Film> films = jdbc.query(query, mapper, directorId);

        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        films.forEach(this::getGenresAndLikesAndDirectors);

        return films;
    }
}
