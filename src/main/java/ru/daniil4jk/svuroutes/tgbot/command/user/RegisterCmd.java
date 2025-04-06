package ru.daniil4jk.svuroutes.tgbot.command.user;

import jakarta.persistence.PersistenceException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ServiceIntegratedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.db.entity.EventEntity;
import ru.daniil4jk.svuroutes.tgbot.db.entity.RequestEntity;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.BooleanInlineKeyboard;

@Component
public class RegisterCmd extends ServiceIntegratedBotCommand {
    public RegisterCmd() {
        super("register", "register to event");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public RegisterCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    private static final String emptyUsername = """
            Мы обнаружили, что у вас, в вашем телеграм аккаунте не указан username.
            Мы не можем создать заявку пока он пуст т.к. без него в случае
            непредвиденной ситуации администратор не сможет вам написать.
            Создайте себе username, после чего пропишите /start,
            а затем попробуйте создать заявку снова""";
    private static final String maxRequestsReached = """
            Не удалось сохранить заявку, на данный момент на экскурсию подано
            слишком много заявок\uD83D\uDE14, но вы можете попробовать позже,
            вдруг администрация отклонит часть заявок и место освободится\uD83D\uDE42""";
    private static final String firstNameMessage = "Введите ваше имя";
    private static final String lastNameMessage = "Введите вашу фамилию";
    private static final String ageMessage = "Введите ваш возраст";
    private static final String classMessage = "Введите номер вашего класса/курса";
    private static final String schoolMessage = "Введите название вашего учебного заведения (например: МАОУ СОШ №1)";
    private static final String acceptMessage = "Анкета успешно заполнена, проверьте ее перед отправкой";

    //TODO create admin request consumer cmd in special admin group

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        SimpleExecuter executer = (SimpleExecuter) absSender;
        if (getUserService().get(chatId).getUsername().isEmpty()) {
            executer.sendSimpleTextMessage(emptyUsername, chatId);
            return;
        }

        EventEntity event = getEventService().get(Long.parseLong(strings[0]));
        if (!event.canAddRequest()) {
            executer.sendSimpleTextMessage(maxRequestsReached, chatId);
            return;
        }

        var request = new RequestEntity();
        request.setEvent(event);
        request.setUser(getUserService().get(chatId));
        getMessageService().addExpectedEvent(chatId, getFirstName(chatId, executer, request));
    }

    private ExpectedEvent<Message> getFirstName(long chatId, SimpleExecuter executer,
                                                RequestEntity request) {
        var message = SendMessage.builder().text(firstNameMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 3) throw new IllegalArgumentException(
                    "Длинна имени не может быть меньше 3 символов");
            request.setFirstName(m.getText());
            getMessageService().addExpectedEvent(chatId, getLastName(chatId, executer, request));
        })
        .firstNotification(message)
        .notification(message)
                .onException(e -> SendMessage.builder()
                        .text(e.getLocalizedMessage())
                        .chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getLastName(long chatId, SimpleExecuter executer,
                                               RequestEntity request) {
        var message = SendMessage.builder().text(lastNameMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 3) throw new IllegalArgumentException(
                    "Длинна фамилии не может быть меньше 3 символов");
            request.setLastName(m.getText());
            getMessageService().addExpectedEvent(chatId, getAge(chatId, executer, request));
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getAge(long chatId, SimpleExecuter executer,
                                          RequestEntity request) {
        var message = SendMessage.builder().text(ageMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            int age;
            try {
                age = Integer.parseInt(m.getText());
                if (age < 8 || age > 80) throw new IllegalArgumentException("Некорректный возраст");
                request.setAge(age);
                getMessageService().addExpectedEvent(chatId, getClass(chatId, executer, request));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Введите ваш возраст в виде числа");
            }
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getClass(long chatId, SimpleExecuter executer,
                                          RequestEntity request) {
        var message = SendMessage.builder().text(classMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            int classNumber;
            try {
                classNumber = Integer.parseInt(m.getText());
                if (classNumber < 1 || classNumber > 11) throw new IllegalArgumentException("Такого класса не существует");
                request.setClassNumber(classNumber);
                getMessageService().addExpectedEvent(chatId, getSchool(chatId, executer, request));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Введите ваш класс в виде числа");
            }
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getSchool(long chatId, SimpleExecuter executer,
                                          RequestEntity request) {
        var message = SendMessage.builder().text(schoolMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 6) throw new IllegalArgumentException(
                    "Длинна названия учебного заведения не может быть меньше 6 символов");
            request.setSchoolName(m.getText());
            getQueryService().addExpectedEvent(chatId, getAccept(chatId, executer, request));
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<CallbackQuery> getAccept(long chatId, SimpleExecuter executer,
                                                   RequestEntity request) {
        var event = request.getEvent();
        var message = SendMessage.builder()
                .replyMarkup(new BooleanInlineKeyboard("Все верно", "Отмена"))
                .text(acceptMessage + String.format("""
                        \nЭкскурсия - %s
                        Имя - %s
                        Фамилия - %s
                        Возраст - %d
                        Класс - %d
                        Учебное заведение - %s
                        """, event.getName() + "#" + event.getId(),
                        request.getFirstName(), request.getLastName(),
                        request.getAge(), request.getClassNumber(), request.getSchoolName()))
                .chatId(chatId)
                .build();

        return new ExpectedEvent<CallbackQuery>(q -> {
            if (String.valueOf(true).equals(q.getData())) {
                try {
                    var savedRequest = getRequestService().save(request);
                    executer.sendSimpleTextMessage(
                            String.format("""
                                            Заявка на экскурсию %s зарегистрирована
                                            Номер заявки для техподдержки: %d""",
                                    savedRequest.getEvent().getName(), savedRequest.getId()),
                            q.getMessage().getChatId()
                    );
                } catch (PersistenceException e) {
                    executer.sendSimpleTextMessage(maxRequestsReached, q.getMessage().getChatId());
                }
            } else if (String.valueOf(false).equals(q.getData())) {
                executer.sendSimpleTextMessage(String
                        .format("Заявка на экскурсию %s успешно отменена",
                                request.getEvent().getName()), chatId);


            } else {
                throw new IllegalArgumentException("CallbackQuery.data() will return only " +
                        "\"true\" or \"false\"");
            }
        })
        .firstNotification(message)
        .notification(message)
        .removeOnException(false);
    }
}