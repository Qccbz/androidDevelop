package q.baselibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class QMobile {

    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * recommend default thread pool size according to system available processors, {@link #getDefaultThreadPoolSize()}
     **/
    public static final int DEFAULT_THREAD_POOL_SIZE = getDefaultThreadPoolSize();

    private QMobile() {
        throw new AssertionError();
    }

    /**
     * get recommend default thread pool size
     *
     * @return if 2 * availableProcessors + 1 less than 8, return it, else return 8;
     * @see {@link #getDefaultThreadPoolSize(int)} max is 8
     */
    public static int getDefaultThreadPoolSize() {
        return getDefaultThreadPoolSize(8);
    }

    /**
     * get recommend default thread pool size
     *
     * @param max
     * @return if 2 * availableProcessors + 1 less than max, return it, else return max;
     */
    public static int getDefaultThreadPoolSize(int max) {
        int availableProcessors = 2 * Runtime.getRuntime().availableProcessors() + 1;
        return availableProcessors > max ? max : availableProcessors;
    }

    /**
     * 检查sim卡状态
     *
     * @param context
     * @return
     */
    public static boolean checkSimState(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT
                || tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
            return false;
        }

        return true;
    }

    /**
     * 获取imei
     */
    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTelephonyMgr.getDeviceId();
        if (imei == null) {
            imei = "000000000000000";
        }
        return imei;
    }

    public static String getPhoneImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }
}
