package q.baselibrary.utils;

import android.content.Context;

public class QScreen {

    private QScreen() {
        throw new AssertionError();
    }

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
    public static int getScreenWidth(Context context) {
        return (int) context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * @param context 上下文
     * @category 屏幕高度px
     */
    public static int getScreenHeight(Context context) {
        return (int) context.getResources().getDisplayMetrics().heightPixels;
    }
}
