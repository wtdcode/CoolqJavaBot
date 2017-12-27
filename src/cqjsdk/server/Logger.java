package cqjsdk.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
    private static Calendar calendar;
    private static DateFormat  dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");

    public static void Log(String string){
        calendar = Calendar.getInstance();
        System.out.println(dateFormat.format(calendar.getTime())+ string);
    }
}
