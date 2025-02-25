package ru.daniil4jk.svuroutes.tgbot.command.admin;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ProtectedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.expected.ExpectedEvent;

import java.util.Comparator;
import java.util.List;

@Component
public class GetFileRef extends ProtectedBotCommand {
    private static final IllegalArgumentException notSupportedFileException =
            new IllegalArgumentException("В сообщении отсутствует файл, или " +
                    "данный тип файлов не поддерживается");

    public GetFileRef() {
        super("getfileref", "wait file and returning telegram InputFile id");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public GetFileRef(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void protectedExecute(AbsSender absSender, long chatId, String[] strings) {
        SimpleExecuter simpleExecuter = ((SimpleExecuter) absSender);
        var message = SendMessage.builder().text("Вышлите файл, для которого хотите получить id")
                .chatId(chatId).build();
        getMessageService().addExpectedEvent(chatId, new ExpectedEvent<Message>(
                m -> {
                    if (m.hasPhoto()) processPhoto(simpleExecuter, chatId, m.getPhoto());
                    else if (m.hasVideo()) processVideo(simpleExecuter, chatId, m.getVideo());
                    else throw notSupportedFileException;
                })
        .firstNotification(message)
        .notification(message)
        .removeOnException(false));
    }

    private void processPhoto(SimpleExecuter executer, long chatId, List<PhotoSize> photos) {
        String fileId = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow().getFileId();
        executer.sendSimpleTextMessage("id для этого ИЗОБРАЖЕНИЯ:", chatId);
        executer.sendSimpleTextMessage(fileId, chatId);
    }

    private void processVideo(SimpleExecuter executer, long chatId, Video video) {
        executer.sendSimpleTextMessage("id для этого ВИДЕО:", chatId);
        executer.sendSimpleTextMessage(video.getFileId(), chatId);
    }
}
