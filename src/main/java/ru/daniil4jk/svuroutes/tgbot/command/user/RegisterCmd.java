package ru.daniil4jk.svuroutes.tgbot.command.user;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ServiceIntegratedBotCommand;
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

    private static final String emptyUsername = "Мы обнаружили, что у вас, " +
            "в вашем телеграм аккаунте не указан username. " +
            "Мы не можем создать заявку пока он пуст т.к. без него в случае " +
            "непредвиденной ситуации администратор не сможет вам написать. " +
            "Создайте себе username, после чего пропишите /start, " +
            "а затем попробуйте создать заявку снова";
    private static final String firstNameMessage = "Введите ваше имя";
    private static final String lastNameMessage = "Введите вашу фамилию";
    private static final String ageMessage = "Введите ваш возраст";
    private static final String classMessage = "Введите номер вашего класса/курса";
    private static final String schoolMessage = "Введите название вашего учебного заведения (например: МАОУ СОШ №1)";
    private static final String acceptMessage = "Анкета успешно заполнена, проверьте ее перед отправкой";

    //TODO create admin request consumer cmd in special admin group

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        if (getUserService().get(chatId).getUsername().isEmpty()) {
            SimpleExecuter executer = (SimpleExecuter) absSender;
            executer.sendSimpleTextMessage(emptyUsername, chatId);
        }

        var request = new RequestEntity();
        request.setEvent(getEventService().get(Long.parseLong(strings[0])));
        request.setUser(getUserService().get(chatId));
        getMessageService().addExpectedEvent(chatId, getFirstName(chatId, absSender, request));
    }

    private ExpectedEvent<Message> getFirstName(long chatId, AbsSender absSender,
                                                RequestEntity request) {
        var message = SendMessage.builder().text(firstNameMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 3) throw new IllegalArgumentException(
                    "Длинна имени не может быть меньше 3 символов");
            request.setFirstName(m.getText());
            getMessageService().addExpectedEvent(chatId, getLastName(chatId, absSender, request));
        })
        .firstNotification(message)
        .notification(message)
                .onException(e -> SendMessage.builder()
                        .text(e.getLocalizedMessage())
                        .chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getLastName(long chatId, AbsSender absSender,
                                               RequestEntity request) {
        var message = SendMessage.builder().text(lastNameMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 3) throw new IllegalArgumentException(
                    "Длинна фамилии не может быть меньше 3 символов");
            request.setLastName(m.getText());
            getMessageService().addExpectedEvent(chatId, getAge(chatId, absSender, request));
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage())
                .chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getAge(long chatId, AbsSender absSender,
                                          RequestEntity request) {
        var message = SendMessage.builder().text(ageMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            int age;
            try {
                age = Integer.parseInt(m.getText());
                if (age < 8 || age > 80) throw new IllegalArgumentException("Некорректный возраст");
                request.setAge(age);
                getMessageService().addExpectedEvent(chatId, getClass(chatId, absSender, request));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Введите ваш возраст в виде числа");
            }
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getClass(long chatId, AbsSender absSender,
                                          RequestEntity request) {
        var message = SendMessage.builder().text(classMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            int classNumber;
            try {
                classNumber = Integer.parseInt(m.getText());
                if (classNumber < 1 || classNumber > 11) throw new IllegalArgumentException("Такого класса не существует");
                request.setClassNumber(classNumber);
                getMessageService().addExpectedEvent(chatId, getSchool(chatId, absSender, request));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Введите ваш класс в виде числа");
            }
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<Message> getSchool(long chatId, AbsSender absSender,
                                          RequestEntity request) {
        var message = SendMessage.builder().text(schoolMessage).chatId(chatId).build();

        return new ExpectedEvent<Message>(m -> {
            if (m.getText().length() < 6) throw new IllegalArgumentException(
                    "Длинна названия учебного заведения не может быть меньше 6 символов");
            request.setSchoolName(m.getText());
            getQueryService().addExpectedEvent(chatId, getAccept(chatId, absSender, request));
        })
        .notification(message)
        .onException(e -> SendMessage.builder()
                .text(e.getLocalizedMessage()).chatId(chatId).build())
        .removeOnException(false);
    }

    private ExpectedEvent<CallbackQuery> getAccept(long chatId, AbsSender absSender,
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

        SimpleExecuter executer = (SimpleExecuter) absSender;
        return new ExpectedEvent<CallbackQuery>(q -> {
            if (String.valueOf(true).equals(q.getData())) {

                var savedRequest = getRequestService().createNew(request.getFirstName(),
                        request.getLastName(), request.getAge(), request.getClassNumber(),
                        request.getSchoolName(), request.getEvent(), request.getUser());

                executer.sendSimpleTextMessage(String
                        .format("""
                                Заявка на экскурсию %s зарегистрирована
                                Номер заявки для техподдержки: %d""",
                                savedRequest.getEvent().getName(), savedRequest.getId()), chatId);

            } else if (String.valueOf(false).equals(q.getData())) {

                executer.sendSimpleTextMessage(String
                        .format("Заявка на экскурсию %s успешно отменена",
                                request.getEvent().getName()), chatId);

            } else {
                throw new IllegalArgumentException("CallbackQuery.data() will be return only " +
                        "\"true\" or \"false\"");
            }
        })
        .firstNotification(message)
        .notification(message)
        .removeOnException(false);
    }
}