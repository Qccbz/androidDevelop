package q.baselibrary.utils;

import android.content.Context;
import android.util.Log;

public class QLog {

    private static boolean isLog = false;// log开关
    public static boolean isStackTrace = false;// 堆栈打印开关
    private static String TAG = "umlxe";

    public static String getClsName() {
        StackTraceElement[] stackTrace = Thread.getAllStackTraces().get(
                Thread.currentThread());
        int len = stackTrace.length;
        StackTraceElement st = stackTrace[len - 1];
        return st.getClassName();
    }

    public static void getTag() {
        if (isStackTrace)
            TAG = getClsName();
    }

    /**
     * @category 信息
     */
    public static void i(String log) {
        getTag();
        DIYLog(LogType.I, TAG, log);
    }

    /**
     * @category 调试
     */
    public static void d(String log) {
        getTag();
        DIYLog(LogType.D, TAG, log);
    }

    /**
     * @category 异常
     */
    public static void e(String log) {
        getTag();
        DIYLog(LogType.E, TAG, log);
    }

    /**
     * @category 警告
     */
    public static void w(String log) {
        getTag();
        DIYLog(LogType.W, TAG, log);
    }

    /**
     * @category 详细
     */
    public static void v(String log) {
        getTag();
        DIYLog(LogType.V, TAG, log);
    }

    /**
     * @category syso
     */
    public static void O(String log) {
        getTag();
        DIYLog(LogType.O, TAG, log);
    }

    /**
     * @param type 日志类型
     * @param log  Log正文
     * @category 日志输出
     */
    public static void DIYLog(int type, String tag, String log) {
        if (isLog) {
            switch (type) {
                case LogType.I:
                    Log.i(tag, log);
                    break;
                case LogType.E:
                    Log.e(tag, log);
                    break;
                case LogType.D:
                    Log.d(tag, log);
                    break;
                case LogType.W:
                    Log.w(tag, log);
                    break;
                case LogType.V:
                    Log.v(tag, log);
                    break;
                case LogType.O:
                    System.out.println(log);
                    break;
            }
        }
    }

    /**
     * @category 信息
     */
    public static void i(String tag, String log) {
        DIYLog(LogType.I, tag, log);
    }

    /**
     * @category 调试
     */
    public static void d(String tag, String log) {
        DIYLog(LogType.D, tag, log);
    }

    /**
     * @category 异常
     */
    public static void e(String tag, String log) {
        DIYLog(LogType.E, tag, log);
    }

    /**
     * @category 警告
     */
    public static void w(String tag, String log) {
        DIYLog(LogType.W, tag, log);
    }

    /**
     * @category 详细
     */
    public static void v(String tag, String log) {
        DIYLog(LogType.V, tag, log);
    }

    public static String getClassName(Context context) {
        Class<?> cl = context.getClass();
        String nameString = cl.getName();
        int last = nameString.lastIndexOf(".");
        return nameString.substring(last);
    }

    /**
     * @category 信息
     */
    public static void i(Context tag, String log) {
        DIYLog(LogType.I, getClassName(tag), log);
    }

    /**
     * @category 调试
     */
    public static void d(Context tag, String log) {
        DIYLog(LogType.D, getClassName(tag), log);
    }

    /**
     * @category 异常
     */
    public static void e(Context tag, String log) {
        DIYLog(LogType.E, getClassName(tag), log);
    }

    /**
     * @category 警告
     */
    public static void w(Context tag, String log) {
        DIYLog(LogType.W, getClassName(tag), log);
    }

    /**
     * @category 详细
     */
    public static void v(Context tag, String log) {
        DIYLog(LogType.V, getClassName(tag), log);
    }

    /**
     * @category 日志类型
     */
    public static class LogType {
        public static final int I = 0x01;
        public static final int D = 0x02;
        public static final int E = 0x03;
        public static final int W = 0x04;
        public static final int V = 0x05;
        public static final int O = 0x06;
    }

}
