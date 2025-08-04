package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review {
    private int reviewId;

    private int filmId;

    private int userId;

    private String content;

    private Boolean isPositive;

    private LocalDateTime createdAt;

    private int useful;
}
