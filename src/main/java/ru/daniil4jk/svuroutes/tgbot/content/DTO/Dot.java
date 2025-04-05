package ru.daniil4jk.svuroutes.tgbot.content.DTO;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.assets.HasAttributes;

import java.util.List;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Dot implements HasAttributes {
    private Long id;
    private String name;
    private String about;
    private InputFile image;
    private InputFile video;
    private List<InputMedia> mediaGroup;
    private GPS gps;

    public Optional<InputFile> getImage() {
        return Optional.ofNullable(image);
    }

    public Optional<InputFile> getVideo() {
        return Optional.ofNullable(video);
    }

    public Optional<List<InputMedia>> getMediaGroup() {
        return Optional.ofNullable(mediaGroup);
    }

    public Optional<GPS> getGps() {
        return Optional.ofNullable(gps);
    }
}
