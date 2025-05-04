package ru.daniil4jk.svuroutes.tgbot.keyboard.processing;

import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.keyboard.processing.assets.AbstractKeyboardCmdCallHandler;

@Setter
@Component
public class KeyboardStaticCmdCallHandler extends AbstractKeyboardCmdCallHandler {
    private static final String[] emptyStringArray = new String[0];

    @Override
    public boolean canAccept(String text, boolean onlyText) {
        if (onlyText) return CommandTag.contains(text);
        try {
            boolean containsNum = CommandTag.contains(Integer.parseInt(text));
            if (!containsNum) throw new IllegalArgumentException();
            return true;
        } catch (IllegalArgumentException e) {
            return CommandTag.contains(text);
        }
    }

    public void accept(String text, long chatId, boolean onlyText) {
        CommandTag tag;
        if (onlyText) {
            tag = CommandTag.normalValueOf(text);
        } else {
            try {
                boolean containsNum = CommandTag.contains(Integer.parseInt(text));
                if (!containsNum) throw new IllegalArgumentException();
                tag = CommandTag.normalValueOf(Integer.parseInt(text));
            } catch (IllegalArgumentException e) {
                tag = CommandTag.normalValueOf(text);
            }
        }
        execute(tag, chatId, emptyStringArray);
    }
}
