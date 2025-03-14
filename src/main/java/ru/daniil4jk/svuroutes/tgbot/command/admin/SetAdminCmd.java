package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.AdminKeyboard;

@Slf4j
@Component
public class SetAdminCmd extends ProtectedBotCommand {
    @Autowired
    private AdminKeyboard adminKeyboard;

    public SetAdminCmd() {
        super("setadmin", "set user as administrator");
        setOnlyAdminAccess(true);
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public SetAdminCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
        setOnlyAdminAccess(true);
    }

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        getMessageService().addExpectedEvent(chatId, ask((SimpleExecuter) absSender, chatId));
    }

    private ExpectedEvent<Message> ask(SimpleExecuter executer, long chatId) {
        SendMessage notification = SendMessage.builder()
                .text(getMessageMap().get(CommandData.GIVE_ADMIN).getText())
                .chatId(chatId)
                .build();

        return new ExpectedEvent<Message>(m -> {
            String username = m.getText();
            getQueryService().addExpectedEvent(chatId, setAdmin(executer, chatId, username));
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build())
        .removeOnException(false);
    }

    private ExpectedEvent<CallbackQuery> setAdmin(SimpleExecuter executer, long chatId, String userName) {
        SendMessage notification = SendMessage.builder()
                .text(String.format("Вы точно хотите дать %s права АДМИНИСТРАТОРА? Он получит ТЕ ЖЕ ПРАВА что и вы!",
                                userName))
                .replyMarkup(new BooleanKeyboard("Да, я хочу это сделать", "Нет"))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<CallbackQuery>(q -> {
            String username = userName;
            username = username.replace("@", "");
            var user = getUserService().getByUsername(username);
            getUserService().update(user, u -> u.setAdmin(true));
            executer.sendSimpleTextMessage(
                    String.format("Пользователь: %s теперь имеет права администратора!",
                            username), q.getMessage().getChatId());
            executer.nonExceptionExecute(SendMessage.builder()
                    .text("Вы теперь администратор")
                    .chatId(user.getId())
                    .replyMarkup(adminKeyboard)
                    .build());
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text("No value present".equals(e.getLocalizedMessage()) ?
                        "Этот пользователь еще не зарегистрирован в боте" :
                        e.getLocalizedMessage())
                .chatId(chatId)
                .build())
        .removeOnException(true)
        .cancelTrigger(BooleanKeyboard.Data.FALSE)
        .cancelText("Отменено");
    }
}
