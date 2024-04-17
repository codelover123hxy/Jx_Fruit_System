package team.CowsAndHorses.util;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date calculateDate(Date date, Integer diff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, diff);
        return calendar.getTime();
    }
}