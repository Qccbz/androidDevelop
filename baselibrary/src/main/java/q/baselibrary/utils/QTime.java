package q.baselibrary.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QTime {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private QTime() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }


    public static String Long2Word(long time) {
        return date2Word(new Date(time), 0);
    }

    public static String Long2Word2(long time) {
        return date2Word(new Date(time), 4);
    }

    public static String Long2WordYear(long time) {
        return date2Word(new Date(time), 1);
    }

    public static String Long2WordYMD(long time) {
        return date2Word(new Date(time), 2);
    }

    /**
     * @param date
     * @param flag 0年月日时分秒 1年月日 2年月日时分
     * @return
     */
    public static String date2Word(Date date, int flag) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String week = null;
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                week = "周一";
                break;
            case Calendar.TUESDAY:
                week = "周二";
                break;
            case Calendar.WEDNESDAY:
                week = "周三";
                break;
            case Calendar.THURSDAY:
                week = "周四";
                break;
            case Calendar.FRIDAY:
                week = "周五";
                break;
            case Calendar.SATURDAY:
                week = "周六";
                break;
            case Calendar.SUNDAY:
                week = "周日";
                break;

        }
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        int am_pm = cal.get(Calendar.AM_PM);

        String yearStr = year > 9 ? "" + year : "0" + year;
        String monthStr = month > 9 ? "" + month : "0" + month;
        String dayStr = day > 9 ? "" + day : "0" + day;

        String hourStr = hour > 9 ? "" + hour : "0" + hour;
        if (am_pm == Calendar.PM) {
            hour += 12;
            hourStr = hour > 9 ? "" + hour : "" + hour;
        }
        String minStr = min > 9 ? "" + min : "0" + min;
        String secStr = sec > 9 ? "" + sec : "0" + sec;

        switch (flag) {
            case 0:
                return "" + yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr
                        + ":" + minStr + ":" + secStr;
            case 5:
                return "" + yearStr + monthStr + dayStr + hourStr + minStr + secStr;
            case 4:
                return "" + yearStr + "." + monthStr + "." + dayStr + "  "
                        + hourStr + ":" + minStr;
            case 1:
                return "" + yearStr + "-" + monthStr + "-" + dayStr;
            case 2:
                return "" + yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr
                        + ":" + minStr;
            case 3:
                return "" + year % 2000 + "-" + monthStr + "-" + dayStr + " "
                        + hourStr + ":" + minStr;
            default:
                return "";
        }

    }

    public static double OLELong2Double(long time) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(time);
        buf.flip();

        return buf.getDouble();
    }

    public static String OLELong2Word(long time) {
        try {
            double d = OLELong2Double(time);
            Date date = OLEtoUTC(d);
            return date2Word(date, 0);
        } catch (Exception e) {
            return "";
        }
    }

    public static String OLE2Word(double time) {
        try {
            Date date = OLEtoUTC(time);
            return date2Word(date, 0);
        } catch (Exception e) {
            return "";
        }
    }

    public static long OLELong2Long(long time) {
        try {
            double d = OLELong2Double(time);
            Date date = OLEtoUTC(d);
            return date.getTime();
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * @category 秒的单位转换
     */
    public static String TransformSecond(int second) {

        /**
         * 附近的人时间规则： 小于60秒 显示秒 大于60秒小于1小时 显示分 大于1小时小于24小时 显示时 大于24小时小于30天 显示天
         * 大于30天小于12月 显示月 大于12月 显示年 到月就够用了
         * */

        if (second < 60) {
            return second + "秒 前";
        } else if (second < 3600) {
            return (second / 60) + "分钟 前";
        } else if (second < 86400) {
            return (second / 3600) + "小时 前";
        } else if (second < 2592000) {
            return (second / 86400) + "天 前";
        } else if (second < 31104000) {
            return (second / 2592000) + "月 前";
        } else {
            return (second / 31104000) + "年 前";
        }
    }

    /**
     * 将OLE时间格式转化为UTC时间格式
     *
     * @param ole
     * @return
     * @throws ParseException
     */
    public static Date OLEtoUTC(double ole) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("1899-12-30");
        return new Date((long) (ole * 24 * 3600 * 1000) + date.getTime());
    }

    /**
     * 将UTC时间格式转化为OLE时间格式 OLE 相对于1900年的按天表示的double型的时间
     *
     * @param utc 相对于1970年的豪秒时间
     * @return
     * @throws ParseException
     */
    public static double UTCtoOLE(long utc) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date0 = sdf.parse("1899-12-30");
        return (utc - date0.getTime()) / (24 * 3600 * 1000.0);
    }

    /**
     * @category 字符串年，转OLE double
     */
    public static double String2OLE(String text) throws ParseException {
        Date d = String2Date(text, "yy-MM-dd");
        return UTCtoOLE(d.getTime());
    }

    public static Date String2Date(String str, String format) {
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据秒获得计算时间
     */
    public static String getTimeStringSecond(long time, String strk) {
        String result = "";
        String CH = "ch";
        String sch = "";
        if (time > -1) {
            int hour = 0;
            int minute = 0;
            int second = 0;

            hour = (int) (time / 3600);
            minute = (int) ((time - hour * 3600) / 60);
            second = (int) (time - hour * 3600 - minute * 60);

            String str = "";

            if (hour > 0) {
                if (CH.equals(strk)) {
                    sch = "小时";
                    // sch = ":";
                }
                str = Integer.toString(hour) + sch;
                if (hour < 10)
                    str = "0" + str;

                result = result + str;
            }

            if (CH.equals(strk)) {
                sch = "分";
                // sch = ":";
            }
            str = Integer.toString(minute) + sch;
            if (minute < 10)
                str = "0" + str;

            result = result + str;

            str = Integer.toString(second);

            if (second < 10)
                str = "0" + str;

            if (CH.equals(strk)) {
                str += "秒";
            }

            result = result + str;
        }

        return result;
    }


    /**
     * 时间计数格式显示
     *
     * @param time 时间以秒为单位
     * @param tag  时间显示格式 0格式 00:12:23 1格式 1小时2分3秒
     * @return
     */
    public static String getTimeCount(long time, int tag) {

        String result = "";
        String sch = "";
        if (time > -1) {
            int hour = 0;
            int minute = 0;
            int second = 0;

            hour = (int) (time / 3600);
            minute = (int) ((time - hour * 3600) / 60);
            second = (int) (time - hour * 3600 - minute * 60);

            String str = "";

            if (hour > 0) {
                if (tag == 1) {
                    sch = "小时";
                } else if (tag == 0) {
                    sch = ":";
                }
                str = Integer.toString(hour) + sch;
                if (hour < 10)
                    str = "0" + str;

                result = result + str;
            }

            if (tag == 1) {
                sch = "分";
            } else if (tag == 0) {
                sch = ":";
            }

            str = Integer.toString(minute) + sch;
            if (minute < 10)
                str = "0" + str;

            result = result + str;

            str = Integer.toString(second);

            if (second < 10)
                str = "0" + str;

            if (tag == 1) {
                str += "秒";
            }
            result = result + str;
        }
        return result;
    }

    public static int getMinuteCount(long time) {
        if (time > -1) {
            int hour = (int) (time / 3600);
            int minute = (int) ((time - hour * 3600) / 60);
            return hour * 60 + minute;
        }
        return 0;
    }

    public static String getTimeStringSecond(long time) {
        return getTimeStringSecond(time, "ch");
    }

    /**
     * 获取指定日期当天的00:00:00或23:59:59
     *
     * @param date
     * @param isZero是否获取零点
     * @return
     */
    public static long get24HourMillis(Date date, boolean isZero) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (!isZero) {
            calendar.add(Calendar.DATE, 1);
        }

        int day = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.set(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (isZero ? calendar.getTimeInMillis() : calendar
                .getTimeInMillis() - 1000);
    }
}
