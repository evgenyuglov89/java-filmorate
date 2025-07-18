MERGE INTO "genres" g
USING (VALUES (1, 'Комедия')) AS vals("id", "name")
ON g."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name") VALUES (vals."id", vals."name");

MERGE INTO "genres" g
USING (VALUES (2, 'Драма')) AS vals("id", "name")
ON g."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name") VALUES (vals."id", vals."name");

MERGE INTO "genres" g
USING (VALUES (3, 'Мультфильм')) AS vals("id", "name")
ON g."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name") VALUES (vals."id", vals."name");

MERGE INTO "genres" g
USING (VALUES (4, 'Триллер')) AS vals("id", "name")
ON g."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name") VALUES (vals."id", vals."name");

MERGE INTO "genres" g
USING (VALUES (5, 'Документальный')) AS vals("id", "name")
ON g."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name") VALUES (vals."id", vals."name");

MERGE INTO "genres" g
USING (VALUES (6, 'Боевик')) AS vals("id", "name")
ON g."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name") VALUES (vals."id", vals."name");


MERGE INTO "mpa_rating" m
USING (VALUES (1, 'G', 'У фильма нет возрастных ограничений')) AS vals("id", "name", "description")
ON m."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name", "description") VALUES (vals."id", vals."name", vals."description");

MERGE INTO "mpa_rating" m
USING (VALUES (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями')) AS vals("id", "name", "description")
ON m."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name", "description") VALUES (vals."id", vals."name", vals."description");

MERGE INTO "mpa_rating" m
USING (VALUES (3, 'PG-13', 'Детям до 13 лет просмотр не желателен')) AS vals("id", "name", "description")
ON m."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name", "description") VALUES (vals."id", vals."name", vals."description");

MERGE INTO "mpa_rating" m
USING (VALUES (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'))
AS vals("id", "name", "description")
ON m."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name", "description") VALUES (vals."id", vals."name", vals."description");

MERGE INTO "mpa_rating" m
USING (VALUES (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён')) AS vals("id", "name", "description")
ON m."id" = vals."id"
WHEN NOT MATCHED THEN
  INSERT ("id", "name", "description") VALUES (vals."id", vals."name", vals."description");
