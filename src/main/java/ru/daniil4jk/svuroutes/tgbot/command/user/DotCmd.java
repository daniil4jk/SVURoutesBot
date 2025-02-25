package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.assets.ServiceIntegratedBotCommand;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Dot;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
@Component
public class DotCmd extends ServiceIntegratedBotCommand {
    @Autowired
    private Map<Long, Dot> dotMap;
    @Autowired
    private ScheduledThreadPoolExecutor sheduledExecutor;

    private static final String yaMapId = "3Afcaf9de0966f04189714f4a1704405ba89859518221946d6787704c954642f35";

    private static final String[] linkForFormat = new String[]
            {
                    "<a href=\"" +
                    "https://yandex.ru/maps/20672/severouralsk/?l=sat%2Cskl&ll=",
                    "%2C",
                    "&mode=usermaps&source=constructorLink&um=constructor%" + yaMapId + "&z=19" +
                            "\">тык</a>"
            };

    public DotCmd() {
        super("dot", "description about dot");
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public DotCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, long chatId, @NotNull String[] strings) {
        try {
            var dot = dotMap.get(Long.parseLong(strings[0]));
            if (dot == null) throw new NoSuchElementException();
            String[] textParts = getBotConfig().isSplitDotText() ?
                    splitText(getText(dot)) : nonSplitText(getText(dot));

            if (dot.getMediaGroup().isPresent() &&
                    !dot.getMediaGroup().get().isEmpty()) {
                absSender.execute(SendMediaGroup.builder()
                        .medias(dot.getMediaGroup().get())
                        .chatId(chatId)
                        .build());
            }
            if (dot.getImage().isPresent() && dot.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .video(dot.getVideo().get())
                        .chatId(chatId)
                        .build());
                absSender.execute(SendPhoto.builder()
                        .photo(dot.getImage().get())
                        .caption(textParts[0])
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else if (dot.getImage().isPresent()) {
                absSender.execute(SendPhoto.builder()
                        .photo(dot.getImage().get())
                        .caption(textParts[0])
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else if (dot.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .video(dot.getVideo().get())
                        .caption(textParts[0])
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else {
                absSender.execute(SendMessage.builder()
                        .text(textParts[0])
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            }
            for (int i = 1; i < textParts.length; i++) {
                if (textParts[i] != null && !textParts[i].isEmpty()) {
                    absSender.execute(SendMessage.builder()
                            .text(textParts[i])
                            .chatId(chatId)
                            .parseMode("HTML")
                            .build());
                }
            }
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchElementException e) {
            SimpleExecuter executer = (SimpleExecuter) absSender;
            executer.sendSimpleTextMessage("Точки с таким id не существует", chatId);
        }
    }

    private String getText(Dot dot) {
        return (dot.getName() +
                (!(dot.getAbout() == null || dot.getAbout().isEmpty()) ? "\n\n" + dot.getAbout() : "")
                + (dot.getGps().isPresent() ? "\n\nПосмотреть на карте: " +
                linkForFormat[0] + dot.getGps().get().getLongitude() + linkForFormat[1] +
                dot.getGps().get().getLatitude() + linkForFormat[2] : ""));//.split("\n");
    }

    private String[] splitText(String text) {
        return text.split("\n");
    }

    private String[] nonSplitText(String text) {
        return new String[]{text};
    }
}
