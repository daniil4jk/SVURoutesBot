package ru.daniil4jk.svuroutes.tgbot.expected.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.daniil4jk.svuroutes.tgbot.expected.services.assets.ExpectedSomethingService;

@Service
public class ExpectedMessageService extends ExpectedSomethingService<Message> {

}
