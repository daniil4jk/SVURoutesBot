package ru.daniil4jk.svuroutes.tgbot.db.service.assets;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;

public interface UserService extends TService<UserEntity> {
    UserEntity createNew(Long id, User user);
    UserEntity getByUsername(String username);
}
