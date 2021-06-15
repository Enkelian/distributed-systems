package util;

import TheOffice.Date;
import TheOffice.DateTime;
import TheOffice.Time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeConverter {

    public static DateTime toDateTime(LocalDateTime ldt){
        return new DateTime(
                new Date(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth()),
                new Time(ldt.getHour(), ldt.getMinute(), ldt.getSecond())
        );
    }

    public static LocalDateTime fromDateTime(DateTime dt){
        return LocalDateTime.of(
                LocalDate.of(dt.date.year, dt.date.month, dt.date.day),
                LocalTime.of(dt.time.hour, dt.time.minute, dt.time.second)
        );
    }


}
