package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Feed {
    private Integer eventId;
    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer entityId;
}
