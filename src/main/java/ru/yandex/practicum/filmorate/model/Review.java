package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private int useful;
}
