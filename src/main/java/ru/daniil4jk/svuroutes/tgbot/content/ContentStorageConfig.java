package ru.daniil4jk.svuroutes.tgbot.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.File;
import java.io.IOException;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "content.filename")
public class ContentStorageConfig {
    private String dots;
    private String routes;
    private String messages;

    @PostConstruct
    public void createFiles() {
        var files = new File[]{new File(routes), new File(messages)};
        for (File file : files) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    log.warn(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        var mapper =  new JsonMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        return mapper;
    }
}
