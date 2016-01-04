package q.baselibrary.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import q.baselibrary.R;

/**
 * 显示工具类
 *
 * @author www
 */
public class UIToolKit {


    /**
     * @param context  上下文
     * @param dipValue dip值
     * @category dip转px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param context 上下文
     * @category dip比例参数
     */
    public static int getScale(Context context) {
        return (int) context.getResources().getDisplayMetrics().density;
    }

    /**
     * @param context 上下文
     * @category dip比例参数
     */
    public static float getScaleFloat(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * @param context 上下文
     * @author jinghq
     * @category 屏幕宽度px
     */
    public static int GetScreenWidth(Context context) {
        return (int) context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * @param context 上下文
     * @category 屏幕高度px
     */
    public static int getScreenHeight(Context context) {
        return (int) context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 显示长时间Toast
     *
     * @param context
     * @param info
     */
    public static void showToastLong(Context context, String info) {
        if (context == null || info == null)
            return;

        Toast toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
        // toast.setGravity(Gravity.CENTER, 0, -30);
        toast.show();
    }

    /**
     * 显示短时间Toast
     *
     * @param context
     * @param info
     */
    public static void showToastShort(Context context, String info) {
        if (context == null || info == null || "".equals(info))
            return;

        Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        // mToast.setGravity(Gravity.CENTER, 0, -30);
        mToast.show();
    }

    /**
     * 显示短时间Toast
     *
     * @param context
     * @param stringId
     */
    public static void showToastShort(Context context, int stringId) {
        if (context == null || stringId < 0) {
            return;
        }
        String info = context.getResources().getString(stringId);
        if (info != null && !"".equals(info)) {
            Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    /**
     * 显示短时间Toast
     *
     * @param context
     * @param info
     */
    public static void showCustemToastShort(Context context, String info) {
        if (context == null || info == null || "".equals(info))
            return;

        Toast mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        View mView = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView mTextView = (TextView) mView.findViewById(R.id.mycustom_toast);
        mTextView.setText(info);

        mToast.setView(mView);
        mToast.show();
    }

    /**
     * service 调用的toast short显示
     *
     * @param context
     * @param info
     */
    public static void serviceToastShort(final Context context,
                                         final String info) {
        if (context == null || info == null)
            return;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context.getApplicationContext(), info,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * just like press HOME
     */
    public static void showMainWindow(Context context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }


    /**
     * 跳转到系统发送短信界面
     *
     * @param context
     * @param number  目标号码
     * @param content 内容
     */
    public static void jumpToSendSMS(Context context, String number,
                                     String content) {
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.putExtra("address", number);
        it.putExtra("sms_body", content);
        it.setType("vnd.android-dir/mms-sms");
        context.startActivity(it);
    }


    /**
     * @param context 上下文
     * @return 在前台true 不在前台false
     * @see 程序是否在前台运行
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the device
        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        String packageName = context.getApplicationContext().getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    /**
     * ScrollView中计算listView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
                MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
