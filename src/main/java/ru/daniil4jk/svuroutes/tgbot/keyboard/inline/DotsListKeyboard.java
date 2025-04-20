package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Dot;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;
import ru.daniil4jk.svuroutes.tgbot.keyboard.inline.assets.TListKeyboard;

import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Component
public class DotsListKeyboard extends TListKeyboard<Dot> {
    @Autowired
    public DotsListKeyboard(Collection<Dot> dots, KeyboardConfig config) {
        super(dots.stream().sorted(Comparator.comparingLong(Dot::getId)).toList(),
                CommandTag.DOT, config);
    }

    public DotsListKeyboard(Collection<Dot> dots, KeyboardConfig config, boolean sort) {
        super(sort ?
                dots.stream().sorted(Comparator.comparingLong(Dot::getId)).toList()
                : dots,
                CommandTag.DOT, config);
    }

    @Override
    protected String getName(Dot dot) {
        return dot.getName();
    }

    @Override
    protected Long getId(Dot dot) {
        return dot.getId();
    }
}
