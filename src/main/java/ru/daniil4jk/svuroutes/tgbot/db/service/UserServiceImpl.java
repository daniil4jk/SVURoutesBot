package ru.daniil4jk.svuroutes.tgbot.db.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.daniil4jk.svuroutes.tgbot.bot.BotConfig;
import ru.daniil4jk.svuroutes.tgbot.db.entity.UserEntity;
import ru.daniil4jk.svuroutes.tgbot.db.repository.UserRepository;
import ru.daniil4jk.svuroutes.tgbot.db.service.assets.UserService;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final Predicate<UserEntity> notRemoved =
            user -> !user.isRemoved();
    @Autowired
    private UserRepository repository;
    @Autowired
    private BotConfig config;

    @Override
    public UserEntity createNew(Long id, User user) {
        return save(new UserEntity(id, new ArrayList<>(), user.getUserName(), false, false));
    }

    @Override
    public UserEntity getByUsername(String username) {
        return repository.getByUsernameContainsIgnoreCase(username).filter(notRemoved).orElseThrow();
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return repository.save(userEntity);
    }

    @Override
    public UserEntity get(long id) {
        return repository.findById(id).filter(notRemoved).orElseThrow();
    }

    @Override
    public UserEntity get(UserEntity userEntity) {
        return get(userEntity.getId());
    }

    @Override
    public boolean contains(long id) {
        return repository.existsByIdAndRemoved(id, false);
    }

    @Override
    public boolean contains(UserEntity userEntity) {
        return contains(userEntity.getId());
    }

    @Override
    public void update(long id, Consumer<UserEntity> updates) {
        var entity = get(id);
        updates.accept(entity);
        save(entity);
    }

    @Override
    public void update(UserEntity userEntity, Consumer<UserEntity> updates) {
        update(userEntity.getId(), updates);
    }

    @Override
    public void remove(long id) {
        update(id, e -> e.setRemoved(true));
    }

    @Override
    public void remove(UserEntity userEntity) {
        remove(userEntity.getId());
    }

    @PostConstruct
    private void checkAdminRoleOfMainAdmin() {
        if (!get(config.getAdminChatId()).isAdmin()) {
            update(config.getAdminChatId(), u -> u.setAdmin(true));
        }
    }
}
