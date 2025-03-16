package ru.daniil4jk.svuroutes.tgbot.command.admin;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanKeyboard;

@Component
public class RemoveEventCmd extends ProtectedBotCommand {
    public RemoveEventCmd() {
        super("removeevent", "remove existing event");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public RemoveEventCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    protected void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        getMessageService().addExpectedEvent(chatId, getEventRemoveExpectedEvent(
                (SimpleExecuter) absSender, chatId, Integer.parseInt(strings[0])
        ));
    }

    private static final String CANCEL_TRIGGER = "ОТМЕНИТЬ";

    private ExpectedEvent<Message> getEventRemoveExpectedEvent(SimpleExecuter executer, long chatId, int eventId) {
        EventEntity event = getEventService().get(eventId);
        SendMessage notification = SendMessage.builder()
                .text(getMessageMap().get(CommandData.ADMIN_REMOVE_EVENT).getText() +
                        String.format("""
                                \nid экскурсии - %d
                                Маршрут экскурсии - %s
                                На экскурсию записано %d человек
                                Если вы действительно хотите сделать это - введите "УДАЛИТЬ %d ЭКСКУРСИЮ" (именно большими буквами)
                                Если вы не хотите удалять - введите "%s"
                                """, event.getId(),
                                event.getName(),
                                event.getRequestEntities().size(),
                                event.getId(),
                                CANCEL_TRIGGER))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<Message>(m -> {
            if (String.format("УДАЛИТЬ %d ЭКСКУРСИЮ", event.getId()).equals(m.getText())) {
                getEventService().remove(eventId);
                executer.sendSimpleTextMessage(
                        String.format("Экскурсия %s успешно удалена", event.getId()), m.getChatId()
                );
            } else {
                throw new IllegalArgumentException();
            }

        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
        .text("Я вас не понимаю. На всякий случай удаление экскурсии отменено.")
                 .chatId(chatId)
                 .build())
        .removeOnException(true)
        .cancelTrigger(CANCEL_TRIGGER)
        .cancelText("Удаление экскурсии отменено.");
    }
}
