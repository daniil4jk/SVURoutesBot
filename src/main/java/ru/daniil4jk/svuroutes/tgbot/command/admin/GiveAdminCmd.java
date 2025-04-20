package ru.daniil4jk.svuroutes.tgbot.command.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.content.CommandMessageService;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.expected.services.ExpectedCallbackQueryService;
import ru.daniil4jk.svuroutes.tgbot.expected.services.ExpectedMessageService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.AdminKeyboard;

@Slf4j
@Component
public class GiveAdminCmd extends ProtectedBotCommand {
    @Autowired
    private UserService userService;
    @Autowired
    private CommandMessageService commandMessageService;
    @Autowired
    private ExpectedCallbackQueryService callbackQueryService;
    @Autowired
    private ExpectedMessageService messageService;
    @Autowired
    private AdminKeyboard adminKeyboard;

    public GiveAdminCmd() {
        super("addadmin", "add administrator rules", CommandTag.GIVE_ADMIN);
        setOnlyAdminAccess(true);
    }

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        messageService.addExpectedEvent(chatId, ask((SimpleExecuter) absSender, chatId));
    }

    private ExpectedEvent<Message> ask(SimpleExecuter executer, long chatId) {
        SendMessage notification = SendMessage.builder()
                .text(commandMessageService.get(CommandTag.GIVE_ADMIN).getText())
                .chatId(chatId)
                .build();

        return new ExpectedEvent<Message>(m -> {
            String username = m.getText();
            callbackQueryService.addExpectedEvent(chatId, setAdmin(executer, chatId, username));
        })
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId)
                .build())
        .removeOnException(false);
    }

    private ExpectedEvent<CallbackQuery> setAdmin(SimpleExecuter executer, long chatId, final String username) {
        SendMessage notification = SendMessage.builder()
                .text("Вы точно хотите дать " + username + " права АДМИНИСТРАТОРА? Он получит ТЕ ЖЕ ПРАВА что и вы! " +
                                "(вы не можете дать права администратора пользователю, еще не заходившему в бот ни разу)")
                .replyMarkup(new BooleanInlineKeyboard("Да, я хочу это сделать", "Нет"))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<CallbackQuery>(q -> {
            String usernameWithoutTag = username.replace("@", "");
            UserEntity user = userService.getByUsername(usernameWithoutTag);
            user.setAdmin(true);
            user = userService.save(user);

            executer.sendSimpleTextMessage(
                    "Пользователь: " + username + " теперь имеет права администратора!",
                    q.getMessage().getChatId());
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
                        "Этот пользователь еще не заходил в бота ни разу" :
                        e.getLocalizedMessage())
                .chatId(chatId)
                .build())
        .removeOnException(true)
        .cancelTrigger(BooleanInlineKeyboard.Data.FALSE)
        .cancelText("Отменено");
    }
}
