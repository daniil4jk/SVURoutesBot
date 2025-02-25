package ru.daniil4jk.svuroutes.tgbot.keyboard.inline;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;
import ru.daniil4jk.svuroutes.tgbot.keyboard.KeyboardConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RouteDotsListKeyboards {
    @Bean
    public Map<Route, DotsListKeyboard>
    routeDotsListKeyboard(Collection<Route> routes, KeyboardConfig config) {
        Map<Route, DotsListKeyboard> result = new HashMap<>();
        for (Route route : routes) {
            result.put(route, new DotsListKeyboard(route.getDots(), config, false));
        }
        return result;
    }
}
