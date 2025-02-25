package ru.daniil4jk.svuroutes.tgbot.bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
    private String botUsername;
    private String token;
    private long adminChatId;
    private long reviewChatId;
    private long suggestionChatId;
    private boolean splitDotText;
}