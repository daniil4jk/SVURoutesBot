package ru.daniil4jk.svuroutes.tgbot.content.DTO.assets;

import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Optional;

public interface HasAttributes {
    Long getId();
    String getName();
    String getAbout();
    Optional<InputFile> getImage();
    Optional<InputFile> getVideo();
}
