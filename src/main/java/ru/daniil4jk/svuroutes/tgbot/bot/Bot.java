package ru.daniil4jk.svuroutes.tgbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.daniil4jk.svuroutes.tgbot.bot.assets.MultithreadingLongPollingCommandBot;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.DefaultSimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandService;
import ru.daniil4jk.svuroutes.tgbot.expected.handlers.ExpectedCallbackQueryHandler;
import ru.daniil4jk.svuroutes.tgbot.expected.handlers.ExpectedMessageHandler;
import ru.daniil4jk.svuroutes.tgbot.keyboard.DefaultKeyboardService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.KeyboardDynamicCmdCallHandler;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.KeyboardStaticCmdCallHandler;

import java.io.Serializable;

@Slf4j
@Component
public class Bot extends MultithreadingLongPollingCommandBot implements SimpleExecuter {
    private final DefaultSimpleExecuter defaultSimpleExecutor = new DefaultSimpleExecuter(this);
    private final BotConfig config;
    @Autowired @Lazy
    private ExpectedMessageHandler expectedMessageHandler;
    @Autowired @Lazy
    private ExpectedCallbackQueryHandler expectedCallbackQueryHandler;
    @Autowired @Lazy
    private KeyboardDynamicCmdCallHandler dynamicKeyboardDataHandler;
    @Autowired @Lazy
    private KeyboardStaticCmdCallHandler staticKeyboardDataHandler;
    @Autowired @Lazy
    private DefaultKeyboardService keyboardService;

    public Bot(BotConfig config, CommandService commandService) {
        super(config.getToken());
        this.config = config;
        getReporter().setUserLogging(true);
        registerAll(commandService.getCommands());
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            processMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            processCallbackQuery(update.getCallbackQuery());
        }
    }

    public void processMessage(Message message) {
        if (message.hasText() && dynamicKeyboardDataHandler.canAccept(message)) {
            dynamicKeyboardDataHandler.accept(message);
        } else if (message.hasText() && staticKeyboardDataHandler.canAccept(message)) {
            staticKeyboardDataHandler.accept(message);
        } else if (expectedMessageHandler.hasEvent(message)) {
            expectedMessageHandler.accept(message);
        } else {
            weDontKnowWhatThisIs(message.getChatId());
        }
    }

    public void processCallbackQuery(CallbackQuery query) {
        if (dynamicKeyboardDataHandler.canAccept(query)) {
            dynamicKeyboardDataHandler.accept(query);
        } else if (staticKeyboardDataHandler.canAccept(query)) {
            staticKeyboardDataHandler.accept(query);
        } else if (expectedCallbackQueryHandler.hasEvent(query)) {
            expectedCallbackQueryHandler.accept(query);
        } else {
            weDontKnowWhatThisIs(query.getMessage().getChatId());
        }
    }

    public void weDontKnowWhatThisIs(long chatId) {
        nonExceptionExecute(SendMessage.builder()
                .text("Я не понимаю вашу команду, пожалуйста, напишите по другому")
                .chatId(chatId)
                .replyMarkup(keyboardService.getKeyboardByStatus(chatId))
                .build());
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        try {
            return super.execute(method);
        } catch (TelegramApiRequestException e) {
            if (!e.getMessage().contains("Too Many Requests")) {
                throw e;
            }
        }
        return null;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T nonExceptionExecute(Method method) {
        return defaultSimpleExecutor.nonExceptionExecute(method);
    }

    @Override
    public <T extends Serializable> T sendSimpleTextMessage(String messageText, long chatId) {
        return defaultSimpleExecutor.sendSimpleTextMessage(messageText, chatId);
    }
}
