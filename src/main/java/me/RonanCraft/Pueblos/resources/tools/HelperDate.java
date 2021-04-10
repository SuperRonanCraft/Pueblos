package me.RonanCraft.Pueblos.resources.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HelperDate {

    public static String getDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
    }

    public static Date getDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
    }

    public static Date getDate() {
        return Calendar.getInstance().getTime();
    }
}