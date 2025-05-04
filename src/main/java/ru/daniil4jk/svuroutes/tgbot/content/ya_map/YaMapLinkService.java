package ru.daniil4jk.svuroutes.tgbot.content.ya_map;

import org.springframework.stereotype.Service;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class YaMapLinkService {
    private final Map<String, YaMapLinkConstructor> map = new HashMap<>();

    public YaMapLinkService(Collection<Route> routes) {
        for (Route route : routes) {
            String linkId = route.getYaMapLinkId();
            map.put(linkId, new YaMapLinkConstructor(linkId));
        }
    }

    public YaMapLinkConstructor get(String linkId) {
        return map.get(linkId);
    }
}
