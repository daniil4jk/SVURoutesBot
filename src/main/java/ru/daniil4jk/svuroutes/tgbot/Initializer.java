package ru.daniil4jk.svuroutes.tgbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
class Initializer {
    @Autowired
    private Collection<LongPollingBot> longPollingBots;
    private final Set<BotSession> sessions = new HashSet<>();

    @EventListener(ApplicationReadyEvent.class)
    public void register() {
        try {
            var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            for (var bot : longPollingBots) {
                sessions.add(telegramBotsApi.registerBot(bot));
            }
        } catch (TelegramApiException e) {
            log.error("Регистрация бота не удалась", e);
        }
    }

    @Async
    @Scheduled(fixedRate = 60000L)
    protected void checkStatus() {
        for (var session : sessions) {
            if (!session.isRunning()) {
                session.start();
            }
        }
    }
}
