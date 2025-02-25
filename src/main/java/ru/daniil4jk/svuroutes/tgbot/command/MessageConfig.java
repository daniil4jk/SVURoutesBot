package ru.daniil4jk.svuroutes.tgbot.command;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "message")
public class MessageConfig {
    private boolean splitDotText;
    private boolean showDots;
    private boolean showRoutes;
}
