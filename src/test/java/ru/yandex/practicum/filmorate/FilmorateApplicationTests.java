package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@JdbcTest
@Import(TestStorageConfig.class)
@AutoConfigureTestDatabase
class FilmoRateApplicationTests {
	private User user;
	private User user2;
	private Film film;
	private Film film2;
	private Film film3;

	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreDbStorage;
	private final MpaDbStorage mpaDbStorage;
	private final JdbcTemplate jdbc;

	@Autowired
	public FilmoRateApplicationTests(
			UserDbStorage userStorage,
			FilmDbStorage filmStorage,
			GenreDbStorage genreDbStorage,
			MpaDbStorage mpaDbStorage,
			JdbcTemplate jdbc
	) {
		this.userStorage = userStorage;
		this.filmStorage = filmStorage;
		this.genreDbStorage = genreDbStorage;
		this.mpaDbStorage = mpaDbStorage;
		this.jdbc = jdbc;
	}

	@BeforeEach
	void setUp() {
		user = User.builder()
				.id(1)
				.login("login")
				.name("name")
				.email("email@email.ru")
				.birthday(LocalDate.of(1992, 2, 8))
				.build();
		user2 = User.builder()
				.id(2)
				.login("login2")
				.name("name2")
				.email("email2@email.ru")
				.birthday(LocalDate.of(1992, 3, 9))
				.build();
		film = Film.builder()
				.id(1)
				.name("name")
				.description("description")
				.releaseDate(LocalDate.of(2000, 5, 11))
				.duration(120)
				.mpa(new Mpa(1, "G", null))
				.build();
		film2 = Film.builder()
				.id(2)
				.name("name2")
				.description("description2")
				.releaseDate(LocalDate.of(2000, 6, 12))
				.duration(160)
				.mpa(new Mpa(1, "G", null))
				.build();
		film3 = Film.builder()
				.id(3)
				.name("name3")
				.description("description3")
				.releaseDate(LocalDate.of(2000, 7, 13))
				.duration(180)
				.mpa(new Mpa(1, "G", null))
				.build();
	}

	@Test
	public void testGetGenres() {
		assertEquals(6, genreDbStorage.genresList().size());
	}

	@Test
	public void testGetGenreById() {
		assertEquals("Комедия", genreDbStorage.findById(1).getName());
		assertEquals("Драма", genreDbStorage.findById(2).getName());
		assertEquals("Мультфильм", genreDbStorage.findById(3).getName());
		assertEquals("Триллер", genreDbStorage.findById(4).getName());
		assertEquals("Документальный", genreDbStorage.findById(5).getName());
		assertEquals("Боевик", genreDbStorage.findById(6).getName());
	}

	@Test
	void testGetMpas() {
		assertEquals(5, mpaDbStorage.mpaList().size());
	}

	@Test
	void testGetMpaById() {
		assertEquals("G", mpaDbStorage.findById(1).getName());
		assertEquals("PG", mpaDbStorage.findById(2).getName());
		assertEquals("PG-13", mpaDbStorage.findById(3).getName());
		assertEquals("R", mpaDbStorage.findById(4).getName());
		assertEquals("NC-17", mpaDbStorage.findById(5).getName());
	}

	@Test
	void testEmptyGetFilms() {
		assertTrue(filmStorage.filmsList().isEmpty());
	}

	@Test
	void testAddFilm() {
		Film filmNew = filmStorage.save(film);

		assertThat(filmNew)
				.hasFieldOrPropertyWithValue("name", "name")
				.hasFieldOrPropertyWithValue("description", "description");
	}

	@Test
	void testGetFilms() {
		filmStorage.save(film);
		filmStorage.save(film2);

		List<Film> films = filmStorage.filmsList();

		assertEquals(2, films.size());

		assertThat(films).extracting("name", "description")
				.containsExactlyInAnyOrder(
						tuple("name", "description"),
						tuple("name2", "description2")
				);
	}

	@Test
	void testUpdateFilm() {
		Film saved = filmStorage.save(film);

		film2.setId(saved.getId());
		filmStorage.update(film2);

		List<Film> films = filmStorage.filmsList();

		assertEquals(1, films.size());

		assertThat(films.get(0))
				.hasFieldOrPropertyWithValue("id", saved.getId())
				.hasFieldOrPropertyWithValue("name", "name2")
				.hasFieldOrPropertyWithValue("description", "description2");
	}

	@Test
	void testEmptyGetUsers() {
		assertTrue(userStorage.usersList().isEmpty());
	}

	@Test
	void testAddUser() {
		User newUser = userStorage.save(user);

		assertThat(newUser)
				.hasFieldOrPropertyWithValue("name", "name")
				.hasFieldOrPropertyWithValue("login", "login")
				.hasFieldOrPropertyWithValue("email", "email@email.ru");
	}

	@Test
	void testGetUsers() {
		userStorage.save(user);
		userStorage.save(user2);
		List<User> users = userStorage.usersList();

		assertEquals(users.size(), 2);
		assertThat(users).extracting("name", "login", "email")
				.containsExactlyInAnyOrder(
						tuple("name", "login", "email@email.ru"),
						tuple("name2", "login2", "email2@email.ru")
				);
	}

	@Test
	void testUpdateUser() {
		User saved = userStorage.save(user);
		user2.setId(saved.getId());
		userStorage.update(user2);
		List<User> users = userStorage.usersList();

		assertEquals(1, users.size());

		assertThat(users.get(0))
				.hasFieldOrPropertyWithValue("id", saved.getId())
				.hasFieldOrPropertyWithValue("name", "name2")
				.hasFieldOrPropertyWithValue("login", "login2");
	}

	@Test
	void testAddFriend() {
		User savedUser1 = userStorage.save(user);
		User savedUser2 = userStorage.save(user2);

		userStorage.addFriend(savedUser1.getId(), savedUser2.getId());

		List<User> friendsUserOne = userStorage.getFriends(savedUser1.getId());

		assertEquals(1, friendsUserOne.size());

		assertThat(friendsUserOne)
				.first()
				.hasFieldOrPropertyWithValue("id", savedUser2.getId())
				.hasFieldOrPropertyWithValue("name", "name2")
				.hasFieldOrPropertyWithValue("email", "email2@email.ru")
				.hasFieldOrPropertyWithValue("login", "login2");
	}

	@Test
	void testDeleteFriend() {
		userStorage.save(user);
		userStorage.save(user2);
		userStorage.addFriend(user.getId(), user2.getId());

		userStorage.deleteFriend(user.getId(), user2.getId());

		List<User> friendsUserOne = userStorage.getFriends(user.getId());

		assertTrue(friendsUserOne.isEmpty());
	}

	@Test
	void testLikeFilm() {
		filmStorage.save(film);
		userStorage.save(user);

		filmStorage.likeFilm(film.getId(), user.getId());

		Integer count = jdbc.queryForObject(
				"SELECT COUNT(*) FROM \"likes\" WHERE \"film_id\" = ? AND \"user_id\" = ?",
				Integer.class, film.getId(), user.getId());

		assertEquals(1, count);
	}

	@Test
	void testRemoveLike() {
		filmStorage.save(film);
		userStorage.save(user);
		filmStorage.likeFilm(film.getId(), user.getId());

		Integer countBefore = jdbc.queryForObject(
				"SELECT COUNT(*) FROM \"likes\" WHERE \"film_id\" = ? AND \"user_id\" = ?",
				Integer.class, film.getId(), user.getId());
		assertEquals(1, countBefore);

		filmStorage.removeLike(film.getId(), user.getId());

		Integer countAfter = jdbc.queryForObject(
				"SELECT COUNT(*) FROM \"likes\" WHERE \"film_id\" = ? AND \"user_id\" = ?",
				Integer.class, film.getId(), user.getId());
		assertEquals(0, countAfter);
	}

	@Test
	void testGetPopularFilms() {
		userStorage.save(user);
		userStorage.save(user2);

		filmStorage.save(film);
		filmStorage.save(film2);
		filmStorage.save(film3);

		filmStorage.likeFilm(film.getId(), user.getId());
		filmStorage.likeFilm(film.getId(), user2.getId());
		filmStorage.likeFilm(film2.getId(), user.getId());

		List<Film> popularFilms = filmStorage.getPopularFilms(2);

		assertEquals(2, popularFilms.size());
		assertEquals(film.getId(), popularFilms.get(0).getId());
		assertEquals(film2.getId(), popularFilms.get(1).getId());
	}
}
