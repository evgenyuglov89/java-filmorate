package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Genre {

    private Integer id;

    private String name;
}
