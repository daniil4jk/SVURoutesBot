package ru.daniil4jk.svuroutes.tgbot.bot.assets;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class MultithreadingLongPollingBot extends TelegramLongPollingBot {
    @Autowired
    private TaskExecutor pool;
    @Getter
    private final UserExceptionReporter reporter = new UserExceptionReporter() {
        @Override
        protected void sendMessage(SendMessage message) throws TelegramApiException {
            execute(message);
        }
    };

    /** @deprecated */
    @Deprecated
    public MultithreadingLongPollingBot(DefaultBotOptions options) {
        super(options);
    }

    public MultithreadingLongPollingBot(DefaultBotOptions options, String botToken) {
        super(options, botToken);
    }

    @Override
    public final void onUpdateReceived(Update update) {
        pool.execute(() -> {
            try {
                asyncUpdateHandle(update);
            } catch (Throwable e) {
                reporter.reportExceptionToUsers(update, e);
            }
        });
    }

    public abstract void asyncUpdateHandle(Update update);
}
