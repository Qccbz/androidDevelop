package q.baselibrary.utils;

import android.os.Environment;

public class MobileToolKit {

    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
