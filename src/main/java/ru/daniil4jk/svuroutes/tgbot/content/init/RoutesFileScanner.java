package ru.daniil4jk.svuroutes.tgbot.content.init;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.daniil4jk.svuroutes.tgbot.content.ContentStorageConfig;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Dot;
import ru.daniil4jk.svuroutes.tgbot.content.DTO.Route;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Configuration
public class RoutesFileScanner {
    @Autowired
    private ContentStorageConfig config;
    @Autowired
    private ObjectMapper mapper;

    @Bean
    public Set<Dot> dots() {
        Set<Dot> dots = new HashSet<>();
        JsonNode node = null;
        try {
            node = mapper.readTree(new File(config.getDots()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        if (node != null) {
            for (JsonNode jsonNode : node) {
                dots.add(mapper.convertValue(jsonNode, Dot.class));
            }
        }

        return dots;
    }

    @Bean
    public Set<Route> routes(Map<Long, Dot> dotMap) {
        Set<Route> routes = new HashSet<>();
        JsonNode node = null;
        try {
            node = mapper.readTree(new File(config.getRoutes()));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        if (node != null) {
            for (JsonNode jsonNode : node) {
                routes.add(mapper.convertValue(jsonNode, Route.class));
            }
        }

        for (Route route : routes) {
            if (route.getDots() == null) route.setDots(new ArrayList<>());
            for (Long dotNumber : route.getDotNumbers()) {
                route.getDots().add(dotMap.get(dotNumber));
            }
        }

        return routes;
    }

    @Bean
    public Map<Long, Dot> dotMap(Collection<Dot> dots) {
        Map<Long, Dot> dotMap = new HashMap<>();
        for (Dot dot : dots) {
            dotMap.put(dot.getId(), dot);
        }
        return dotMap;
    }

    @Bean
    public Map<Long, Route> routeMap(Collection<Route> routes) {
        Map<Long, Route> routeMap = new HashMap<>();
        for (Route route : routes) {
            routeMap.put(route.getId(), route);
        }
        return routeMap;
    }
}
