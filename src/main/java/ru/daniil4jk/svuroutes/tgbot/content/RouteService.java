package ru.daniil4jk.svuroutes.tgbot.content;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.command.CommandTag;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteService implements ContentService<Long, Route> {
    private final Map<Long, Route> routeMap;

    @Override
    public Route get(Long id) {
        return routeMap.get(id);
    }
}
