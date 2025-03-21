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

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        getMessageService().addExpectedEvent(chatId, getSuggestion(chatId, absSender));
    }

    private ExpectedEvent<Message> getSuggestion(long chatId, AbsSender absSender) {
        var notification = SendMessage.builder()
                .text(getMessageMap().get(CommandData.ADD_SUGGESTION).getText())
                .replyMarkup(new CancelKeyboard("Отменить"))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 15) throw new IllegalArgumentException(
                    "Длинна текста не может быть меньше 15 символов"
            );

            getQueryService().addExpectedEvent(chatId, getAccept(chatId, absSender, m));
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<CallbackQuery> getAccept(long chatId, AbsSender absSender, Message suggestionMessage) {
        SimpleExecuter executer = (SimpleExecuter) absSender;

        var notification = SendMessage.builder()
                .text("Ваше предложение: " + suggestionMessage.getText())
                .replyMarkup(new BooleanKeyboard("Все верно", "Отмена"))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<CallbackQuery>(q -> {
            if (!String.valueOf(true).equals(q.getData())) return;

            var acceptedSuggestion = getSuggestionService().save(new SuggestionEntity(
                    null,
                    getUserService().get(suggestionMessage.getChatId()),
                    suggestionMessage.getText(),
                    false
            ));

            executer.sendSimpleTextMessage(
                    String.format("Предложение с номером %d успешно добавлено", acceptedSuggestion.getId()),
                    q.getMessage().getChatId()
            );

            executer.sendSimpleTextMessage(
                    String.format("""
                    Предложение с id: %d
                    От: @%s
                    С текстом: %s
                    """, acceptedSuggestion.getId(),
                            acceptedSuggestion.getUser().getUsernameAsOptional().orElse("не указан"),
                            acceptedSuggestion.getText()),
                    getBotConfig().getSuggestionChatId()
            );
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build())
        .removeOnException(false)
        .cancelTrigger(BooleanKeyboard.Data.FALSE)
        .cancelText("Вы отменили отправку приглашения");
    }
}
