package ru.daniil4jk.svuroutes.tgbot.expected;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class ExpectedEvent<T> {
    private final Consumer<T> event;
    private Function<Exception, BotApiMethodMessage> onException;
    private BotApiMethodMessage firstNotification;
    private BotApiMethodMessage notification;
    private boolean removeOnException = false;
    //private boolean lastEventInChain = false; todo this
    private String cancelTrigger;
    private String cancelText;

    public ExpectedEvent(Consumer<T> event) {
        this.event = event;
    }

    public ExpectedEvent<T> onException(Function<Exception, BotApiMethodMessage> onException) {
        this.onException = onException;
        return this;
    }

    public ExpectedEvent<T> removeOnException(boolean removeOnException) {
        this.removeOnException = removeOnException;
        return this;
    }

//    public ExpectedEvent<T> lastEventInChain(boolean lastEventInChain) {
//        this.lastEventInChain = lastEventInChain;
//        return this;
//    }

    public ExpectedEvent<T> firstNotification(BotApiMethodMessage notification) {
        this.firstNotification = notification;
        return this;
    }

    public ExpectedEvent<T> notification(BotApiMethodMessage notification) {
        this.notification = notification;
        return this;
    }

    public ExpectedEvent<T> cancelTrigger(String cancelTrigger) {
        this.cancelTrigger = cancelTrigger;
        return this;
    }

    public ExpectedEvent<T> cancelText(String cancelText) {
        this.cancelText = cancelText;
        return this;
    }
}
