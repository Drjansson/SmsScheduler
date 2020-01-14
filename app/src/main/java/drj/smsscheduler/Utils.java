package drj.smsscheduler;

import java.util.Calendar;

/**
 * Created by David on 2016-06-07.
 */

public class Utils {

    public static String[] omwMessages = {"omw",
                                        "I'm coming home, I'm coming home, tell the world I'm coming home!",
                                        "You better be prepared, I'm omw home.",
                                        "On my way home.",
                                        "Home in a bit",
                                        "On my bike home now"};

    public static String translateMonths(int month){
        switch(month) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
            default:
                return "No month";
        }
    }


    public static String translateDays(int day){
        switch(day) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return "No day";
        }
    }

    public static String padZero(int date){
        if(date >= 0 && date <10){
            return "0"+date;
        }
        return ""+date;
    }

}
