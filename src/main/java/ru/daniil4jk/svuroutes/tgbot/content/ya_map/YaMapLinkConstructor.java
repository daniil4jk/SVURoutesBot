package ru.daniil4jk.svuroutes.tgbot.content.ya_map;

import ru.daniil4jk.svuroutes.tgbot.content.DTO.GPS;

public class YaMapLinkConstructor {
    public YaMapLinkConstructor(String mapId) {
        this.mapId = mapId;
    }

    private final String mapId;
    private static final String[] basicLink = new String[]
            {
                    "<a href=\"" + "https://yandex.ru/maps/20672/severouralsk/?l=sat%2Cskl&ll=",
                    "%2C",
                    "&mode=usermaps&source=constructorLink&um=constructor%",
                    "&z=19" + "\">клик</a>"
            };

    public String getLinkForPlace(GPS gps) {
        return basicLink[0] + gps.getLongitude() +
                basicLink[1] + gps.getLatitude() +
                basicLink[2] + mapId + basicLink[3];
    }

}
