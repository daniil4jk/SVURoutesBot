package ru.daniil4jk.svuroutes.tgbot.keyboard;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "keyboard")
public class KeyboardConfig {
    private byte maxButtonsInRow;
}
