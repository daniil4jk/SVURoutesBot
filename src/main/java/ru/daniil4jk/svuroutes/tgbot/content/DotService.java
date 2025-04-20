package ru.daniil4jk.svuroutes.tgbot.content;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Dot;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DotService implements ContentService<Long, Dot> {
    private final Map<Long, Dot> dotMap;

    @Override
    public Dot get(Long id) {
        return dotMap.get(id);
    }
}