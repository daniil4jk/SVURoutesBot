package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.MessageEntry;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.DefaultKeyboardService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.KeyboardDynamicCmdCallHandler;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.KeyboardStaticCmdCallHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StartCmd extends BotCommand {
    private static final CommandData messageName = CommandData.START;

    @Autowired
    private DefaultKeyboardService keyboardService;
    @Autowired
    private UserService userService;
    @Autowired
    private Map<CommandData, MessageEntry> messageMap;
    @Autowired @Lazy
    private KeyboardStaticCmdCallHandler staticHandler;
    @Autowired @Lazy
    private KeyboardDynamicCmdCallHandler dynamicHandler;
    @Autowired
    private ScheduledThreadPoolExecutor sheduledExecutor;

    public StartCmd() {
        super("start", "start dialog");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public StartCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (userService.contains(chat.getId())) {
            UserEntity userEntity = userService.get(chat.getId());
            if (!Objects.equals(user.getUserName(),
                    userEntity.getUsername())) {
                userService.update(chat.getId(), u ->
                    u.setUsername(user.getUserName())
                );
            }
            executeWithKeyboard(absSender, chat.getId(),
                    keyboardService.getKeyboardByStatus(userEntity));
        } else {
            userService.createNew(chat.getId(), user);
            log.info("We have a new user!");
        }
        log.info("Somebody used \"/start\" command");

        if (strings.length > 0) {
            sheduledExecutor.schedule(
                    () -> executeStringMappedMessage(chat.getId(), strings),
                    1, TimeUnit.SECONDS);
        }
    }

    //copied from staticCommand
    public void executeWithKeyboard(AbsSender absSender, long chatId, ReplyKeyboard keyboard) {
        var message = messageMap.get(messageName);
        try {
            if (message.getImage().isPresent() && message.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .video(message.getVideo().get())
                        .chatId(chatId)
                        .build());
                absSender.execute(SendVideo.builder()
                        .caption(message.getText())
                        .replyMarkup(keyboard)
                        .video(message.getImage().get())
                        .chatId(chatId)
                        .build());
            } else if (message.getImage().isPresent()) {
                absSender.execute(SendPhoto.builder()
                        .caption(message.getText())
                        .replyMarkup(keyboard)
                        .photo(message.getImage().get())
                        .chatId(chatId)
                        .build());
            } else if (message.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .caption(message.getText())
                        .replyMarkup(keyboard)
                        .video(message.getVideo().get())
                        .chatId(chatId)
                        .build());
            } else {
                absSender.execute(SendMessage.builder()
                        .text(message.getText())
                        .replyMarkup(keyboard)
                        .chatId(chatId)
                        .build());
            }
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private void executeStringMappedMessage(long chatId, String[] strings) {
        String command = strings[0];
        if (staticHandler.canAccept(command, false)) {
            staticHandler.accept(command, chatId, false);
        } else if (dynamicHandler.canAccept(command, false)) {
            dynamicHandler.accept(command, chatId, false);
        }
    }
}
