package ru.daniil4jk.svuroutes.tgbot.command.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.BotConfig;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReviewRequestsCmd extends ProtectedBotCommand {

    @Autowired
    private BotConfig config;

    public ReviewRequestsCmd() {
        super("review", "review requests");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public ReviewRequestsCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    private static final String CANCEL_TRIGGER = "Закончить просмотр заявок";
    private static final String CANCEL_TEXT = "Просмотр заявок окончен";

    @Override
    protected void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        var executer = ((SimpleExecuter) absSender);
        executer.sendSimpleTextMessage(
                getMessageMap().get(CommandData.ADMIN_REQUESTS).getText(), chatId);
        if (getNextRequest(null).isEmpty()) {
            ((SimpleExecuter) absSender).sendSimpleTextMessage(
                    "Новых заявок пока нет", chatId);
        } else {
            showNextRequest(executer, new AtomicLong(chatId), null);
        }
    }

    private ExpectedEvent<CallbackQuery> getRequestRepresentation(
            SimpleExecuter executer, AtomicLong chatId, RequestEntity request) {

        getRequestService().update(request, r -> r.setStatus(RequestEntity.Status.IN_PROGRESS));

        SendMessage notification = SendMessage.builder()
                .text(getMessageMap().get(CommandData.ADMIN_REQUEST).getText() + getRequestString(request))
                .replyMarkup(new BooleanInlineKeyboard("Принять", "Отказать", CANCEL_TRIGGER))
                .chatId(chatId.get())
                .build();

        return new ExpectedEvent<CallbackQuery>(
                q -> {
                    if (chatId.get() != q.getMessage().getChatId()) {
                        chatId.set(q.getMessage().getChatId());
                    }

                    if (BooleanInlineKeyboard.Data.TRUE.equals(q.getData())) {
                        processAccept(executer, request, chatId, true);
                    } else if (BooleanInlineKeyboard.Data.FALSE.equals(q.getData())) {
                        processAccept(executer, request, chatId, false);
                    } else {
                        executer.sendSimpleTextMessage(
                                "Просмотр заявок остановлен", chatId.get());
                        getRequestService().update(request, r ->
                            r.setStatus(RequestEntity.Status.WAITING));
                    }
                }
        )
        .firstNotification(notification)
        .notification(notification)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId.get())
                .build())
        .removeOnException(false)
        .cancelTrigger(BooleanInlineKeyboard.Data.CANCEL)
        .cancelText(CANCEL_TEXT);
    }

    private String getRequestString(RequestEntity request) {

        return String.format("""
                        \nНомер заявки - %d
                        Экскурсия - %s
                        Имя - %s
                        Фамилия - %s
                        Возраст - %d
                        Класс/Курс - %d
                        Название учреждения - %s
                        Юзернейм для обратной связи - @%s
                        """, request.getId(),
                request.getEvent().getName() + "#" + request.getEvent().getId(),
                request.getFirstName(), request.getLastName(),
                request.getAge(), request.getClassNumber(),
                request.getSchoolName(),
                request.getUser().getUsernameAsOptional().orElse("не указан"));
    }

    private void processAccept(SimpleExecuter executer, RequestEntity request,
                               AtomicLong chatId, boolean accept) {
        setAccepted(request, accept);
        notifyUser(executer, request);
        postRequestReview(executer, request);
        showNextRequest(executer, chatId, request.getId());
    }

    private void showNextRequest(SimpleExecuter executer, AtomicLong chatId, Long lastRequestId) {
        var requestOptional = getNextRequest(lastRequestId);
        if (requestOptional.isPresent()) {
            getQueryService().addExpectedEvent(chatId.get(),
                    getRequestRepresentation(executer, chatId, requestOptional.get()));
        } else {
            executer.sendSimpleTextMessage("Заявки кончились", chatId.get());
        }
    }

    private void setAccepted(RequestEntity request, boolean accept) {
        getRequestService().update(request,
                r -> r.setStatus(accept ?
                        RequestEntity.Status.ACCEPTED : RequestEntity.Status.REJECTED));
    }

    private void notifyUser(SimpleExecuter executer, RequestEntity request) {
        request = getRequestService().get(request);
        executer.sendSimpleTextMessage(
                "Ваша заявка под номером " + request.getId() +
                        (RequestEntity.Status.ACCEPTED.equals(request.getStatus()) ?
                        " принята! Вы записаны на экскурсию" :
                        " не принята, попробуйте еще раз или напишите в поддержку"),
                request.getUser().getId());
    }

    private void postRequestReview(SimpleExecuter executer, RequestEntity request) {
        request = getRequestService().get(request);
        executer.sendSimpleTextMessage("Заявка ПРИЯТА\n" +
                getRequestString(request),
                config.getReviewChatId());
    }

    private Optional<RequestEntity> getNextRequest(Long currentId) {
        return getRequestService().getNext(currentId);
    }
}