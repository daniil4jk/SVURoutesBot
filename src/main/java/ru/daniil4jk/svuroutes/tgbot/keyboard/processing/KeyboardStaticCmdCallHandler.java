package ru.daniil4jk.svuroutes.tgbot.keyboard.processing;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.bot.Bot;
import ru.daniil4jk.svuroutes.tgbot.command.CommandData;
import ru.daniil4jk.svuroutes.tgbot.command.CommandService;
import ru.daniil4jk.svuroutes.tgbot.command.admin.AdminPanelCmd;
import ru.daniil4jk.svuroutes.tgbot.command.admin.CreateEventCmd;
import ru.daniil4jk.svuroutes.tgbot.command.admin.GiveAdminCmd;
import ru.daniil4jk.svuroutes.tgbot.command.admin.ReviewRequestsCmd;
import ru.daniil4jk.svuroutes.tgbot.command.user.*;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.AbstractKeyboardCmdCallHandler;

@Setter
@Component
public class KeyboardStaticCmdCallHandler extends AbstractKeyboardCmdCallHandler {
    private static final String[] emptyStringArray = new String[0];
    @Autowired
    private Bot bot;
    @Autowired
    private CommandService commands;

    //todo: change onlyText to recieveNums and revert booleans
    @Override
    public boolean canAccept(String text, boolean onlyText) {
        if (onlyText) return CommandData.contains(text);
        try {
            boolean containsNum = CommandData.contains(Integer.parseInt(text));
            if (!containsNum) throw new IllegalArgumentException();
            return true;
        } catch (IllegalArgumentException e) {
            return CommandData.contains(text);
        }
    }

    public void accept(String text, long chatId, boolean onlyText) {
        CommandData data;
        if (onlyText) {
            data = CommandData.normalValueOf(text);
        } else {
            try {
                boolean containsNum = CommandData.contains(Integer.parseInt(text));
                if (!containsNum) throw new IllegalArgumentException();
                data = CommandData.normalValueOf(Integer.parseInt(text));
            } catch (IllegalArgumentException e) {
                data = CommandData.normalValueOf(text);
            }
        }
        switch (data) {
            case ROUTES -> commands.getCommand(RoutesCmd.class).execute(bot, chatId, emptyStringArray);
            case DOTS ->  commands.getCommand(DotsCmd.class).execute(bot, chatId, emptyStringArray);
            case ABOUT_US ->  commands.getCommand(AboutUsCmd.class).execute(bot, chatId, emptyStringArray);
            case ABOUT_PROJECT -> commands.getCommand(AboutProjectCmd.class).execute(bot, chatId, emptyStringArray);
            case EVENTS -> commands.getCommand(EventsCmd.class).execute(bot, chatId, emptyStringArray);
            case ADMIN_REQUESTS -> commands.getCommand(ReviewRequestsCmd.class).execute(bot, chatId, emptyStringArray);
            case ADMIN_CREATE_EVENT -> commands.getCommand(CreateEventCmd.class).execute(bot, chatId, emptyStringArray);
            case ADMIN_PANEL -> commands.getCommand(AdminPanelCmd.class).execute(bot, chatId, emptyStringArray);
            case GIVE_ADMIN -> commands.getCommand(GiveAdminCmd.class).execute(bot, chatId, emptyStringArray);
            case REQUESTS -> commands.getCommand(RequestsListCmd.class).execute(bot, chatId, emptyStringArray);
            case ADD_SUGGESTION -> commands.getCommand(AddSuggestionCmd.class).execute(bot, chatId, emptyStringArray);
            default -> bot.weDontKnowWhatThisIs(chatId);
        }
    }
}
