package q.baselibrary.utils;

public class QSize {

    /**
     * gb to byte
     **/
    public static final long GB_2_BYTE = 1073741824;
    /**
     * mb to byte
     **/
    public static final long MB_2_BYTE = 1048576;
    /**
     * kb to byte
     **/
    public static final long KB_2_BYTE = 1024;

    private QSize() {
        throw new AssertionError();
    }

    /**
     * 得出B的上级单位字符串
     *
     * @param bsize
     * @return
     */
    public static String parseBytesUnit(long bsize) {
        if (bsize < KB_2_BYTE) {
            return bsize + "B";
        } else if (bsize < MB_2_BYTE) {
            return (bsize / KB_2_BYTE) + "KB";
        } else if (bsize < GB_2_BYTE) {
            return (bsize / MB_2_BYTE) + "M";
        } else {
            return (bsize / GB_2_BYTE) + "G";
        }
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
            tmp = tmp / KB_2_BYTE;
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
}
