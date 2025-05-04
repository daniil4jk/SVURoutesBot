package ru.daniil4jk.svuroutes.tgbot.command.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daniil4jk.svuroutes.tgbot.bot.simpleexecuter.SimpleExecuter;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.command.assets.SimpleBotCommand;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.DotsListKeyboard;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class RouteCmd extends SimpleBotCommand {
    @Autowired
    private Map<Long, Route> tMap;
    @Autowired
    private Map<Route, DotsListKeyboard> routeDotsListKeyboard;

    public RouteCmd() {
        super("route", "description about route", CommandTag.ROUTE);
    }

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public RouteCmd(String commandIdentifier, String description) {
        super(commandIdentifier, description, CommandTag.ROUTE);
    }

    @Override
    public void execute(AbsSender absSender, long chatId, String[] strings) {
        try {
            var route = tMap.get(Long.parseLong(strings[0]));
            if (route == null) throw new NoSuchElementException();
            String text = getText(route);
            var keyboardInFirstMessage = routeDotsListKeyboard.get(route);

            if (route.getMediaGroup().isPresent() &&
                    !route.getMediaGroup().get().isEmpty()) {
                absSender.execute(SendMediaGroup.builder()
                        .medias(route.getMediaGroup().get())
                        .chatId(chatId)
                        .build());
            }
            if (route.getImage().isPresent() && route.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .video(route.getVideo().get())
                        .chatId(chatId)
                        .build());
                absSender.execute(SendPhoto.builder()
                        .photo(route.getImage().get())
                        .caption(text)
                        .replyMarkup(keyboardInFirstMessage)
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else if (route.getImage().isPresent()) {
                absSender.execute(SendPhoto.builder()
                        .photo(route.getImage().get())
                        .caption(text)
                        .replyMarkup(keyboardInFirstMessage)
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else if (route.getVideo().isPresent()) {
                absSender.execute(SendVideo.builder()
                        .video(route.getVideo().get())
                        .caption(text)
                        .replyMarkup(keyboardInFirstMessage)
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            } else {
                absSender.execute(SendMessage.builder()
                        .text(text)
                        .replyMarkup(keyboardInFirstMessage)
                        .chatId(chatId)
                        .parseMode("HTML")
                        .build());
            }
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage(), e);
        } catch (NoSuchElementException e) {
            SimpleExecuter executer = (SimpleExecuter) absSender;
            executer.sendSimpleTextMessage("Маршрута с таким id не существует", chatId);
        }
    }

    private String getText(Route route) {
        return (route.getName() +
                (!(route.getAbout() == null || route.getAbout().isEmpty()) ? "\n\n" + route.getAbout() : "")
                + "\n\nТочки:");
    }
}
