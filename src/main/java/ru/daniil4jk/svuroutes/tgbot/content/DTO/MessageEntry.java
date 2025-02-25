package ru.daniil4jk.svuroutes.tgbot.content.DTO;

import lombok.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.util.List;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MessageEntry {
    private final String text;
    private final InputFile image;
    private final InputFile video;
    private List<InputMedia> mediaGroup;

    public Optional<InputFile> getImage() {
        return Optional.ofNullable(image);
    }

    public Optional<InputFile> getVideo() {
        return Optional.ofNullable(video);
    }

    public Optional<List<InputMedia>> getMediaGroup() {
        return Optional.ofNullable(mediaGroup);
    }
}
