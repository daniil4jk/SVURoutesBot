package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.BotConfig;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.TaggedCommand;
import ru.daniil4jk.svuroutes.tgbot.content.CommandMessageService;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.SuggestionService;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.expected.services.ExpectedCallbackQueryService;
import ru.daniil4jk.svuroutes.tgbot.expected.services.ExpectedMessageService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.DefaultKeyboardService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.CancelReplyKeyboard;

@Component
public class AddSuggestionCmd extends TaggedCommand {
    private static final CancelReplyKeyboard CANCEL_KEYBOARD = new CancelReplyKeyboard("Отменить");
    @Autowired
    private DefaultKeyboardService keyboardService;
    @Autowired
    private CommandMessageService commandMessageService;
    @Autowired
    private ExpectedCallbackQueryService queryService;
    @Autowired
    private SuggestionService suggestionService;
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private ExpectedMessageService messageService;

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public AddSuggestionCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description, CommandTag.ADD_SUGGESTION);
    }

    public AddSuggestionCmd() {
        super("suggestion", "add suggestion", CommandTag.ADD_SUGGESTION);
    }

    private static final String CANCEL_MESSAGE = "Вы отменили отправку предложения/пожелания";

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        messageService.addExpectedEvent(chatId, getSuggestion(chatId, absSender));
    }

    private ExpectedEvent<Message> getSuggestion(long chatId, AbsSender absSender) {
        var notification = SendMessage.builder()
                .text(commandMessageService.get(CommandTag.ADD_SUGGESTION).getText())
                .replyMarkup(CANCEL_KEYBOARD)
                .chatId(chatId)
                .build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 15) throw new IllegalArgumentException(
                    "Длинна текста не может быть меньше 15 символов"
            );

            queryService.addExpectedEvent(chatId, getAccept(chatId, absSender, m));
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .replyMarkup(CANCEL_KEYBOARD)
                .build())
        .removeOnException(false)
        .cancelTrigger("Отменить")
        .cancelText(CANCEL_MESSAGE);
    }

    private ExpectedEvent<CallbackQuery> getAccept(long chatId, AbsSender absSender, Message suggestionMessage) {
        SimpleExecuter executer = (SimpleExecuter) absSender;

        var notification = SendMessage.builder()
                .text("Ваше предложение: " + suggestionMessage.getText())
                .replyMarkup(new BooleanInlineKeyboard("Все верно", "Отмена"))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<CallbackQuery>(q -> {
            if (!String.valueOf(true).equals(q.getData())) return;

            var acceptedSuggestion = suggestionService.createNew(
                    suggestionMessage.getChatId(),
                    suggestionMessage.getText()
            );

            executer.nonExceptionExecute(SendMessage.builder()
                            .text(String.format("Предложение с номером %d успешно добавлено",
                                    acceptedSuggestion.getId()))
                            .chatId(q.getMessage().getChatId())
                            .replyMarkup(keyboardService.getKeyboardByStatus(chatId))
                            .build());

            executer.sendSimpleTextMessage(
                    String.format("""
                    Предложение с id: %d
                    От: @%s
                    С текстом: %s
                    """, acceptedSuggestion.getId(),
                            acceptedSuggestion.getUser().getUsernameAsOptional().orElse("не указан"),
                            acceptedSuggestion.getText()),
                    botConfig.getSuggestionChatId()
            );
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build())
        .removeOnException(false)
        .cancelTrigger(BooleanInlineKeyboard.Data.FALSE)
        .cancelText(CANCEL_MESSAGE);
    }
}
