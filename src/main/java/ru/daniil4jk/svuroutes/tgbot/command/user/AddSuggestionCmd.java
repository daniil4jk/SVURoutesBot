package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ServiceIntegratedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.db.entity.SuggestionEntity;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.CancelKeyboard;

@Component
public class AddSuggestionCmd extends ServiceIntegratedBotCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public AddSuggestionCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    public AddSuggestionCmd() {
        super("suggestion", "add suggestion");
    }

    private static final String ACCEPT_SUGGESTION = "Ваше предложение: ";
    private static final String CANCEL_TRIGGER = "Отменить";
    private static final String CANCEL_TEXT = "Вы отменили отправку приглашения";

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        getMessageService().addExpectedEvent(chatId, getSuggestion(chatId, absSender));
    }

    private ExpectedEvent<Message> getSuggestion(long chatId, AbsSender absSender) {
        var notification = SendMessage.builder()
                .text(getMessageMap().get(CommandData.ADD_SUGGESTION).getText())
                .replyMarkup(new CancelKeyboard(CANCEL_TRIGGER))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<Message>(m -> {

            if (m.getText().length() < 15) throw new IllegalArgumentException(
                    "Длинна текста не может быть меньше 15 символов");

            var suggestion = new SuggestionEntity(
                    null,
                    getUserService().get(m.getChatId()),
                    m.getText(),
                    false
                    );
            getQueryService().addExpectedEvent(chatId, getAccept(chatId, absSender, suggestion, m));
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false)
        .cancelTrigger(CANCEL_TRIGGER)
        .cancelText(CANCEL_TEXT);
    }

    private ExpectedEvent<CallbackQuery> getAccept(long chatId, AbsSender absSender,
                                                   SuggestionEntity suggestion, Message messageToCopy) {
        SimpleExecuter executer = (SimpleExecuter) absSender;

        var message = SendMessage.builder()
                .text(ACCEPT_SUGGESTION + suggestion.getText())
                .replyMarkup(new BooleanKeyboard("Все верно", "Отмена"))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<CallbackQuery>(q -> {
            if (!String.valueOf(true).equals(q.getData())) return;
            var acceptedSuggestion = getSuggestionService().save(suggestion);
            executer.sendSimpleTextMessage(
                    String.format("Предложение с номером %d успешно добавлено", acceptedSuggestion.getId()),
                    q.getMessage().getChatId()
            );
            executer.sendSimpleTextMessage(
                    String.format("""
                    Предложение под номером %d
                    От %s
                    С текстом: %s
                    """, acceptedSuggestion.getId(),
                            acceptedSuggestion.getUser().getUsername().orElse("не указан"),
                            acceptedSuggestion.getText()),
                    q.getMessage().getChatId()
            );
            executer.nonExceptionExecute(CopyMessage.builder()
                    .fromChatId(chatId)
                    .messageId(messageToCopy.getMessageId())
                    .chatId(getBotConfig().getSuggestionChatId())
                    .build());
        })
        .firstNotification(message)
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false)
        .cancelTrigger(String.valueOf(false))
        .cancelText(CANCEL_TEXT);
    }
}
