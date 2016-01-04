package q.baselibrary.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理字符串的工具类
 *
 * @author www
 */
public class StringToolKit {

    public static String Long2Word(long time) {
        return Date2Word(new Date(time), 0);
    }

    public static String Long2Word2(long time) {
        return Date2Word(new Date(time), 4);
    }

    public static String Long2WordYear(long time) {

        return Date2Word(new Date(time), 1);
    }

    public static String Long2WordYMD(long time) {

        return Date2Word(new Date(time), 2);
    }

    /**
     * @param date
     * @param flag 0年月日时分秒 1年月日 2年月日时分
     * @return
     */
    public static String Date2Word(Date date, int flag) {
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
            return Date2Word(date, 0);
        } catch (Exception e) {
            return "";
        }
    }

    public static String OLE2Word(double time) {
        try {
            Date date = OLEtoUTC(time);
            return Date2Word(date, 0);
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
     * 得出B的上级单位字符串
     *
     * @param bsize
     * @return
     */
    public static String parseBytesUnit(long bsize) {
        if (bsize < 1024) {
            return bsize + "B";
        } else if (bsize < 1048576) {
            return (bsize / 1024) + "KB";
        } else if (bsize < 1073741824) {
            return (bsize / 1048576) + "M";
        } else {
            return (bsize / 1073741824) + "G";
        }
    }

    /**
     * @category 获取一个临时文件名
     */
    public static String getTempPictureName(String exStr) {
        String extName = ".tmp";
        if (exStr.startsWith("pic_"))
            extName = ".jpg";
        return exStr + Calendar.getInstance().getTimeInMillis() + extName;
    }

    public static String getCircleTypeName(int index) {
        switch (index) {
            case 0:
                return "团体";
            case 1:
                return "家人";
            case 2:
                return "同学";
            case 3:
                return "朋友";
            case 4:
                return "同事";
            case 5:
                return "群组";
            default:
                return "家人";
        }
    }

    /**
     * @category 米的单位转换
     */
    public static String TransformMeters(int meter) {
        if (meter < 0)
            return "0米";

        if (meter <= 1000) {
            return meter + "米";
        } else {
            int tm = (meter / 1000);
            if (tm > 10000) {
                tm /= 10000;
                return tm + "万公里";
            } else {
                return tm + "公里";
            }
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
     * @category 用特殊字符掩盖标准手机号
     */
    public static String MaskPhoneNumber(String data, String mask) {
        int len = data.length();
        if (len > 0) {
            String num = handleNumber(data);
            if (num.length() == 11) {
                String head = num.substring(0, 3);
                String foot = num.substring(7, 11);
                return head + "****" + foot;
            }
        }
        return "";
    }

    /**
     * @category 验证是不是Email
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return RegexCheckValidity(email, str);
    }

    /**
     * @author jinghq
     * @category 根据正则检查数据有效性
     */
    public static boolean RegexCheckValidity(String data, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(data);
        return m.matches();
    }

    /**
     * 获取字符串的长度 一个汉字算两个字符
     *
     * @param s
     * @return
     */
    public static int getWordCount(String s) {

        s = s.replaceAll("[^\\x00-\\xff]", "**");
        return s.length();
    }

    /**
     * @param type 正则模式 0中文，1英文，2数字
     * @return String
     * @category 过滤字符串符合正则模式的部分
     * @author jinghq
     */
    public static String RegexCheck(String data, int type) {
        switch (type) {
            case 0:
                // TODO 这里手动处理一下正则不能过滤出来的字符
            /* 中文后加x会过滤到的中文有x，不知道是什么原因 */
                String[] sps = {",", "，", ".", "。", "“", "”", "\"", "-", "+", "{",
                        "}", "『", "』", "?", "？", "×", "*", "x", "y", "!", "@", "#",
                        "$", "^", "z", "v", "w", "~", "%", "&", "(", ")", "|", "'",
                        "/", "～", "！", "￥", "…", "（", "）", "—", "：", ".", "》", "《",
                        "；", "‘", "【", "】", "、"};
                for (String s : sps) {
                    data = data.replace(s, "");
                }
                return RegexCheck(data, "[u4e00-u9fa5]");
            case 1:
                return RegexCheck(data, "[^A-Za-z]");
            case 2:
                return RegexCheck(data, "[^0-9]");
        }
        return data;
    }

    /**
     * @param regex 正则表达式
     * @return String
     * @category 过滤字符串符合正则部分的字符
     * @author jinghq
     */
    public static String RegexCheck(String data, String regex) {
        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(data);
            return m.replaceAll("").trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @author jinghq
     * @category 检查字符串是否为IPv4，查点的个数
     */
    public static boolean checkIp(String ip) {
        // 过滤空字符
        ip = ip.replace(" ", "");

        int count = 0;
        char[] chars = ip.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if ('.' == chars[i]) {
                count++;
            }
        }

        if (count != 3) {
            return false;
        } else {
            String[] ipArgs = ip.split("\\.");
            return ipArgs.length != 4 ? false : true;
        }
    }

    /**
     * @param src 要判断的字符串
     * @return 字符串空返回true，不为空返回false
     * @category 判断字符串是否为空
     * @author jinghq
     */
    public static boolean isBlank(String src) {
        if (src != null && src.length() > 0) {
            return !"".equals(src) ? false : true;
        }
        return true;// null
    }

    /**
     * @param src 要判断的字符串
     * @return 字符串空返回false，不为空返回true
     * @category 判断字符串是否不为空
     * @author jinghq
     */
    public static boolean notBlank(String src) {
        return !isBlank(src);
    }

    /**
     * @param oldDate 要格式化的字符串
     * @return String
     * @author jinghq
     * @category 格式化日期
     */
    public static String FormatDate(String oldDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(df.parse(oldDate));
        } catch (Exception e) {
            return oldDate;
        }
    }

    public static String FormatDate1(String oldDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
            return df.format(df.parse(oldDate));
        } catch (Exception e) {
            return oldDate;
        }
    }

    public static String FormatDate2(String oldDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return df.format(df.parse(oldDate));
        } catch (Exception e) {
            return oldDate;
        }
    }

    /**
     * @param str 要格式化的字符串
     * @return Date
     * @author jinghq
     * @category 将字符串转成日期
     */
    public static Date String2Date(String str) {
        return String2Date(str, "yyyy-MM-dd");
    }

    public static Date String2Date1(String str) {
        return String2Date(str, "yyyy年MM月");
    }

    public static Date String2Date2(String str) {
        return String2Date(str, "yyyy-MM-dd HH:mm");
    }

    public static long String2long(String str) {
        long time = 0;
        try {
            time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str))
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long String2long2(String str) {
        long time = 0;
        try {
            time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss|SSS").parse(str))
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static Date String2Date_YY(String str) {
        return String2Date(str, "yy-MM-dd");
    }

    public static Date String2Date(String str, String format) {
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param date
     * @return 如果是今天昨天前天的日期显示文字 其他显示为yy-MM-dd格式的日期
     * @category 传入日期的long型返回格式化的日期
     */
    public static String getDayStyle(long date) {
        String myday = "";
        SimpleDateFormat sdf2 = new SimpleDateFormat("yy-MM-dd");
        Calendar calendar_old = Calendar.getInstance();
        calendar_old.setTimeInMillis(date);
        Calendar calendar_now = Calendar.getInstance();
        int year_now = calendar_now.get(Calendar.YEAR);
        int monthOfYear_now = calendar_now.get(Calendar.MONTH);
        int dayOfMonth_now = calendar_now.get(Calendar.DAY_OF_MONTH);
        int year_old = calendar_old.get(Calendar.YEAR);
        int monthOfYear_old = calendar_old.get(Calendar.MONTH);
        int dayOfMonth_old = calendar_old.get(Calendar.DAY_OF_MONTH);
        // QLog.O("now year:"+year_now+" month:"+monthOfYear_now+" day:"+dayOfMonth_now);
        // QLog.O("old  year:"+year_old+" month:"+monthOfYear_old+" day:"+dayOfMonth_old);
        if (year_old == year_now) {
            if (monthOfYear_old == monthOfYear_now) {
                if (dayOfMonth_old == dayOfMonth_now) {
                    myday = "今天";
                } else if (dayOfMonth_old == dayOfMonth_now - 1) {
                    myday = "昨天";
                } else if (dayOfMonth_old == dayOfMonth_now - 2) {
                    myday = "前天";
                } else {
                    myday = sdf2.format(new Date(date));
                }
            } else {
                myday = sdf2.format(new Date(date));
            }
        } else {
            myday = sdf2.format(new Date(date));
        }
        return myday;
    }

    /**
     * @param args 要转的数组
     * @return ArrayList<String>
     * @author jinghq
     * @category 将数组转成ArrayList<String>
     */
    public static ArrayList<String> getListFromArgs(String[] args) {
        ArrayList<String> data = new ArrayList<String>();
        for (String s : args) {
            data.add(s);
        }
        return data;
    }

    /**
     * @param sex 性别字符串
     * @return int 0:男|1:女
     * @category 获取男女性别的值
     * @author jinghq
     */
    public static int getSexValue(String sex) {
        return "女".equals(sex) ? 1 : 0;
    }

    /**
     * @param sex 值
     * @return String 0:男|1:女
     * @category 获取男女性别的字符串
     * @author jinghq
     */
    public static String getSexString(int sex) {
        return 1 == sex ? "女" : "男";
    }

    /**
     * @param args 要格式化的ArrayList
     * @return String
     * @category 将ArrayList转成通用型参数字符串（中间加：分隔符）
     * @author jinghq
     */
    public static String FormatList4Param(ArrayList<String> args) {
        String param = "";
        for (String s : args) {
            s = s.replace(":", "^#");
            param += s + ":";
        }
        return param.substring(0, param.length() - 1);
    }

    /**
     * @param index 顺序数组
     * @param data  数据
     * @return ArrayList
     * @category 将data数组按照index指定的顺序排序
     * @author jinghq
     */
    public static ArrayList<String> getIndexList(int[] index, String[] data) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i : index) {
            list.add(data[i]);
        }
        return list;
    }

    /**
     * @param buffer ByteBuffer对象
     * @return String
     * @author jinghq
     * @category 将ByteBuffer转成String
     */
    public static String ByteBuffer2String(ByteBuffer buffer) {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 删除"+86"字符
     */
    public static String remove86(String in) {
        String result = in;

        int index = result.indexOf("+86");
        if (index != -1)
            result = result.substring(index + 3);

        return result;
    }

    /**
     * 处理电话号码（去掉“+86”、“-”）
     */
    public static String handleNumber(String number) {
        String result = number.replace("-", "");
        result = remove86(result);
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
     * 转换为计算机数据单位内容
     */
    public static String getByteString(long bytes) {
        // 防止负数
        if (bytes <= 0)
            return "0Byte";

        String str = "", value = "";
        double tmp = bytes, tmp2 = 0;
        int step = 0, index = 0;

        // 首先获得最终转换数据大小和单位判定系数
        while (tmp > 1) {
            tmp2 = tmp;
            tmp = tmp / 1024.0;
            step++;
        }

        // 根据判定系数，获得最终换算单位
        switch (step) {
            case 1:
                str = "Byte";
                break;
            case 2:
                str = "KB";
                break;
            case 3:
                str = "MB";
                break;
            case 4:
                str = "GB";
                break;
            case 5:
                str = "TB";
                break;
            case 6:
                str = "PB";
                break;
            case 7:
                str = "EB";
                break;
            case 8:
                str = "ZB";
                break;
            case 9:
                str = "YB";
                break;
        }

        value = Double.toString(tmp2);
        index = value.indexOf(".");

        if (index != -1) {
            if (value.length() - index > 3)
                if (str.equals("MB")) {
                    value = value.substring(0, (index + 4));
                } else {
                    value = value.substring(0, (index + 2));
                }
        }

        // 合成并返回最终的字符串
        str = value + str;

        return str;
    }

    /**
     * 判断是否是手机号码
     */
    public static boolean isMoblieNumber_old(String number) {
        if (number == null || number.length() == 0) {
            return false;
        }

        String reg = "^(\\+{0,1}|0{0,2})(86){0,1}0{0,1}(13[0-9]|15[0-9]|18[0-9]|14[0-9])[0-9]{8}$";

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(number);

        return m == null ? false : m.find();
    }

    /**
     * 判断是否是手机号码,只要是1开头的就可以
     *
     * @param number
     * @return
     * @since 2014-11-19
     */
    public static boolean isMoblieNumber(String number) {
        if (number == null || number.length() == 0) {
            return false;
        }

        String reg = "^(\\+{0,1}|0{0,2})(86){0,1}0{0,1}(1[0-9][0-9])[0-9]{8}$";

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(number);

        return m == null ? false : m.find();
    }

    /**
     * 判断是否是域名
     */
    public static boolean isIpAddress(String address) {
        if (address == null || address.length() == 0) {
            return false;
        }

        String reg = "^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(address);

        return m == null ? false : m.find();
    }

    /**
     * 判断是否是网址
     */
    public static String getUrl(String address) {
        if (address == null || address.length() == 0) {
            return "";
        }
        String reg = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(address);

        boolean isFind = m == null ? false : m.find();
        if (isFind) {
            return m.group();
        }
        return "";
    }

    public static boolean isUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        String reg = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(url);

        return m == null ? false : m.find();
    }

    /**
     * 判断是否只含有数字和字母
     */
    public static boolean isOnlyNumAndLetter(String str) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }

        String reg = "^[0-9A-Za-z]+$";

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);

        return m == null ? false : m.find();
    }

    /**
     * 是否只含有数字
     **/
    public static boolean isOnlyNum(String str) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }

        String reg = "^[0-9]*$";

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);

        return m == null ? false : m.find();
    }

    /**
     * 是否包含中文的判断
     *
     * @param str
     * @return
     */
    public static boolean isContainsChinese(String str) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(str);
        return matcher.find() ? true : false;
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
        Date d = StringToolKit.String2Date(text, "yy-MM-dd");
        return StringToolKit.UTCtoOLE(d.getTime());
    }

    /**
     * @param text 要截取的字符串
     * @return String
     * @author jinghq
     * @category 截取最后一个字
     */
    public static String getLastWord(String text) {
        if (text == null) {
            return "";
        }
        text = getNameLastWord(text);
        int len = text.length();
        if (len > 1) {
            return text.substring(len - 2, len);
        } else if (len > 0) {
            return text.substring(len - 1, len);
        } else {
            return "";
        }
    }

    /**
     * @author jinghq
     * @category 按照汉字取最后一个字, 没有汉字的返回“”
     */
    public static String getNameLastWord(String text) {
        String c = RegexCheck(text, 0);
        return c.length() == 0 ? "" : c;
    }

    public static final String ToNormalCall_NotUM = "对方未使用UMcall,已切换至普通电话";
    public static final String NoNetWorkConnect = "无法连接至服务器，请检查网络或重试";
    public static final String NoServerConnect = "无法连接至服务器，已切换至系统电话";
    public static final String TimeOut = "无法连接至服务器，请检查网络或重试";

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

    public static String replaceBr(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
