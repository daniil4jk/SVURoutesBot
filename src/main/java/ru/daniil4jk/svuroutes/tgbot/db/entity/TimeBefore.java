package ru.daniil4jk.svuroutes.tgbot.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Getter
public class TimeBefore {
    private static final long millsInDay = 1000 * 60 * 60 * 24;
    private static final long millsInHour = 1000 * 60 * 60;
    private static final long millsInMin = 1000 * 60;

    private final int days;
    private final int hours;
    private final int mins;

    public static TimeBefore of(Date date) {
        long eventTime = date.getTime();
        long currentTime = System.currentTimeMillis();
        long difference = eventTime - currentTime;

        int days = Math.toIntExact(Math.floorDiv(difference, millsInDay));
        difference -= days * millsInDay;
        int hours = Math.toIntExact(Math.floorDiv(difference, millsInHour));
        difference -= hours * millsInHour;
        int mins = Math.toIntExact(Math.floorDiv(difference, millsInMin));

        return new TimeBefore(days, hours, mins);
    }
}
