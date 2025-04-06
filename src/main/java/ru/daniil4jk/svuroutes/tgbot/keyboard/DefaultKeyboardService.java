package ru.daniil4jk.svuroutes.tgbot.keyboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.AdminKeyboard;
import ru.daniil4jk.svuroutes.tgbot.keyboard.reply.DefaultKeyboard;

@Service
public class DefaultKeyboardService {
    @Autowired
    @Qualifier("defaultKeyboard")
    private DefaultKeyboard defaultKeyboard;
    @Autowired
    private AdminKeyboard adminKeyboard;

    @Autowired
    private UserService service;

    public DefaultKeyboard getKeyboardByStatus(long userId) {
        return getKeyboardByStatus(service.get(userId));
    }

    public DefaultKeyboard getKeyboardByStatus(UserEntity user) {
        return user.isAdmin() ? adminKeyboard : defaultKeyboard;
    }
}
