package ru.daniil4jk.svuroutes.tgbot.expected.handlers.assets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;
import ru.daniil4jk.svuroutes.tgbot.expected.services.assets.ExpectedSomethingService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.AdminKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.DefaultKeyboard;

import java.util.Objects;

@Slf4j
public abstract class ExpectedHandlerAbstractImpl<T> implements ExpectedHandler<T> {
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultKeyboard defaultKeyboard;
    @Autowired
    private AdminKeyboard adminKeyboard;
    @Autowired
    private Bot bot;
    @Autowired
    private ExpectedSomethingService<T> expectedService;

    //todo refactor to many methods
    @Override
    public void accept(T t) {
        long id = getChatId(t);
        int stackSizeBeforeEventRunning = expectedService.getStackSize(id);
        var event = expectedService.getLastExpectedEvent(id);
        String content = getContent(t);
        if (Objects.equals(content, event.getCancelTrigger()) &&
                event.getCancelText() != null) {
            bot.nonExceptionExecute(SendMessage.builder()
                    .text(event.getCancelText())
                    .replyMarkup(userService.get(id).isAdmin() ? adminKeyboard : defaultKeyboard)
                    .chatId(id)
                    .build());
            expectedService.removeEvent(id, event);
            return;
        }
        try {
            event.getEvent().accept(t);
            expectedService.removeEvent(id, event);
        } catch (Exception e) {
            if (event.getOnException() != null) {
                bot.nonExceptionExecute(event.getOnException().apply(e));
            } else {
                throw e;
            }
            if (event.isRemoveOnException()) {
                expectedService.removeEvent(id, event);
            }
        } finally {
            if (hasEvent(id) &&
                    !eventAddedNewOne(stackSizeBeforeEventRunning, expectedService.getStackSize(id))) {
                var message = expectedService.getLastExpectedEvent(id).getNotification();
                bot.nonExceptionExecute(message);
            }
        }
    }

    private boolean eventAddedNewOne(int stackSizeBeforeEventRunning,
                                     int stackSizeAfterEventRunning) {
        return stackSizeBeforeEventRunning == stackSizeAfterEventRunning;
    }

    @Override
    public boolean hasEvent(T t) {
        return expectedService.hasExpectedEvent(getChatId(t));
    }

    @Override
    public boolean hasEvent(long chatId) {
        return expectedService.hasExpectedEvent(chatId);
    }

    public abstract long getChatId(T t);

    public abstract String getContent(T t);
}