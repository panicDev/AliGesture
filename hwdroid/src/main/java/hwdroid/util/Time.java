package hwdroid.util;

public class Time {

    private Time() {
    }

    /**
     * The number of milliseconds in a second.
     */
    public static final long HW_SECOND = 1000;

    /**
     * The number of milliseconds in a minute.
     */
    public static final long HW_MINUTE = HW_SECOND * 60;

    /**
     * The number of milliseconds in an hour.
     */
    public static final long HW_HOUR = HW_MINUTE * 60;

    /**
     * The number of milliseconds in a day.
     */
    public static final long HW_DAY = HW_HOUR * 24;

    /**
     * The number of milliseconds in a week.
     */
    public static final long HW_WEEK = HW_DAY * 7;

}
