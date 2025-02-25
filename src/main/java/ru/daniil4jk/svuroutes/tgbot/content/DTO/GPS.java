package ru.daniil4jk.svuroutes.tgbot.content.DTO;

import lombok.*;

@ToString
@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GPS {
    private double latitude;
    private double longitude;
}
