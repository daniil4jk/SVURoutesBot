package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.SimpleBotCommand;
import ru.daniil4jk.svuroutes.tgbot.command.assets.TaggedCommand;
import ru.daniil4jk.svuroutes.tgbot.content.CommandMessageService;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.RequestService;

import java.util.NoSuchElementException;

@Slf4j
@Component
public class RequestCmd extends TaggedCommand {

    @Autowired
    private CommandMessageService commandMessageService;

    @Autowired
    private RequestService requestService;

    public RequestCmd() {
        super("request", "show info by request for user", CommandTag.REQUEST);
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public RequestCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description, CommandTag.REQUEST);
    }

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        try {
            RequestEntity request = requestService.get(Long.parseLong(strings[0]));
            EventEntity event = request.getEvent();
            String requestStatus = switch (request.getStatus()) {
                case WAITING -> "Ожидает рассмотрения";
                case IN_PROGRESS -> "На рассмотрении";
                case ACCEPTED -> "Принята";
                case REJECTED -> "Отклонена";
            };
            absSender.execute(SendMessage.builder()
                    .text(commandMessageService.get(CommandTag.REQUEST).getText() + String.format("""
                            \nЭкскурсия - %s
                            Имя - %s
                            Фамилия - %s
                            Возраст - %d
                            Статус - %s
                            """, event.getName() + "#" + event.getId(),
                            request.getFirstName(), request.getLastName(),
                            request.getAge(), requestStatus))
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchElementException e) {
            SimpleExecuter executer = (SimpleExecuter) absSender;
            executer.sendSimpleTextMessage("Заявки с таким id не существует", chatId);
        }
    }
}
