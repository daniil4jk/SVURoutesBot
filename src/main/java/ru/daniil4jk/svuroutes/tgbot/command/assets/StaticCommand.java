package ru.daniil4jk.svuroutes.tgbot.command.assets;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.content.CommandMessageService;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.MessageEntry;

import java.util.Map;

@Slf4j
public class StaticCommand extends ProtectedBotCommand {
    private final ReplyKeyboard keyboard;
    private MassiveMessageSender sender;
    @Autowired
    private CommandMessageService messageService;

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public StaticCommand(String commandIdentifier, String description,
                         CommandTag tag, ReplyKeyboard keyboard) {
        super(commandIdentifier, description, tag);
        this.keyboard = keyboard;
        setOnlyAdminAccess(false);
    }

    @PostConstruct
    private void postConstruct() {
        sender = new MassiveMessageSender(messageService.get(getTag()), keyboard);
    }

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        sender.execute(absSender, chatId);
    }

    public void executeWithKeyboard(AbsSender absSender, long chatId, ReplyKeyboard keyboard) {
        sender.executeWithKeyboard(absSender, chatId, keyboard);
    }
}
