package q.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    public static boolean isBigFile(String path, long size) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            long len = fis.available();
            return len > size ? true : false;
        } catch (Exception e) {
            return true;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否为大于size的大文件
     *
     * @param f
     * @param size
     * @return
     */
    public static boolean isBigFile(File f, long size) {
        if (f != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                return fis.available() > size ? true : false;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // 有sd卡时的发送图片存储地址
    private static final String PIC_PATH = MobileToolKit.isSDCardEnable() ? Environment
            .getExternalStorageDirectory() + "/umcall/um_temp"
            : "";
    // 有sd卡时的发送小图片存储地址
    private static final String SHORT_PIC_PATH = MobileToolKit.isSDCardEnable() ? Environment
            .getExternalStorageDirectory() + "/umcall/um_tmp"
            : "";

    /**
     * scale image to fixed height and weight
     *
     * @param imagePath
     * @param isMin
     * @return
     */
    private static int getImageScale(String imagePath, boolean isMin) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the
        // bitmap info without having to allocate the
        // memory for its pixels.
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        if (isMin) {
            while (option.outWidth / scale >= 480
                    || option.outHeight / scale >= 960) {
                scale *= 2;
            }
        } else {
            if (option.outWidth > 3000 || option.outHeight > 2000) {
                scale = 3;
            } else if (option.outWidth > 2000 || option.outHeight > 1500) {
                scale = 2;
            }
            // else if (option.outWidth > 1000 || option.outHeight > 1000) {
            // scale = 2;
            // }
        }
        return scale;
    }

    private static int getImageScale(String imagePath, int width, int height) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the
        // bitmap info without having to allocate the
        // memory for its pixels.
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        while (option.outWidth / scale >= width
                || option.outHeight / scale >= height) {
            scale *= 2;
        }
        return scale;
    }

    public static Bitmap getImage(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = getImageScale(path, width, height);
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 获取大小合适的图片,如果抛异常则使用使用规定的图片最小size
     *
     * @param path
     * @return
     */
    public static Bitmap getImage(String path) {
        Bitmap image = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = getImageScale(path, false);
        try {
            image = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {// OutOfMemoryError
            e.printStackTrace();
            options.inSampleSize = getImageScale(path, true);
            image = BitmapFactory.decodeFile(path, options);
        }
        return image;
    }

    public static boolean isBigPicture(String path) {
        return isBigFile(path, 1048576);
    }

    /**
     * @category 将Uri转成Path
     */
    public static String cutUri(String path) {
        String temPath = path.toString();
        int len = temPath.length();
        return len > 7 ? swapesch(temPath.substring(7, len)) : "";
    }

    /**
     * @category UTF-8转路径
     */
    public static String swapesch(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 获取文件名、大小、文件时间、文件格式等信息
     *
     * @param filePath
     * @return
     */
    public static ArrayList<String> getExtra(String filePath) {
        // 文件名、大小、文件时间、文件格式等信息
        ArrayList<String> data = null;
        File f = new File(filePath);

        if (f != null && f.exists()) {
            data = new ArrayList<String>(4);
            data.add(f.getName());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                data.add(StringToolKit.parseBytesUnit(fis.available()));
            } catch (Exception e) {
                data.add("");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            data.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date(f
                    .lastModified())));
            data.add("jpeg");
        }
        return data;
    }

    /**
     * 组装文件名、大小、文件时间、文件格式等信息
     **/
    public static ArrayList<String> getFileExtra(String filePath) {
        ArrayList<String> data = null;
        // 文件名、大小、文件时间、文件格式等信息
        System.gc();
        File file = new File(filePath);

        if (file != null && file.exists()) {
            data = new ArrayList<String>();
            data.add("" + file.getName());
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);
                data.add(StringToolKit.parseBytesUnit(fis.available()));
            } catch (Exception e) {
                data.add("");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Date date = new Date(file.lastModified());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            data.add(sdf.format(date));
            data.add(getExtensionName(file.getName()));
        }
        return data;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 格式化两位小数
     *
     * @param x
     * @param y
     * @return
     */
    public static String getDecimalForTwo(int x, int y) {
        double decimalX = x * 1.0;
        double decimalY = y * 1.0;
        double decimal = decimalX / decimalY;
        decimal *= 100;
        NumberFormat df = new DecimalFormat("#.00");
        return df.format(decimal);
    }

    public static String getXYPercent(int x, int y) {
        double decimalX = x * 1.0;
        double decimalY = y * 1.0;
        double decimal = decimalX / decimalY;
        decimal *= 100;
        NumberFormat df = new DecimalFormat("#");
        return df.format(decimal);
    }

    /**
     * @category 层叠文件扩展信息
     */
    public static String expandExtra(ArrayList<String> data) {
        if (data != null) {
            StringBuffer sb = new StringBuffer();
            for (String s : data) {
                sb.append(s + ":");
            }

            int len = sb.length();
            return len > 1 ? sb.deleteCharAt(len - 1).toString() : "";
        }
        return "";
    }

    /**
     * @category 展开文件扩展信息
     */
    public static ArrayList<String> getExtraInfos(String data) {
        ArrayList<String> temp = new ArrayList<String>();
        String[] s = data.split(":");
        for (String d : s) {
            temp.add(d);
        }
        return temp;
    }

    /**
     * @category 展开文件扩展信息
     */
    public static ArrayList<String> getFileExtraInfos(String data) {
        ArrayList<String> temp = new ArrayList<String>();
        String[] s = data.split(":");
        for (String d : s) {
            if (StringToolKit.notBlank(d)) {
                temp.add(d);
            }
        }
        return temp;
    }

    /**
     * @category 按照文件名获取图片
     */
    public static Bitmap getSDPIC(String fileName) {
        if (MobileToolKit.isSDCardEnable()) {// 检查SD卡可用
            String pathStr = Environment.getExternalStorageDirectory()
                    + "/umcall";
            File root = new File(pathStr);

            if (checkDir(root, true)) {
                String subPathStr = root.getPath() + "/um_file";
                File path = new File(subPathStr);
                if (checkDir(path, true)) {
                    File file = new File(path.getPath() + "/" + fileName);
                    return file.exists() ? getImage(file.getPath()) : null;
                }
            }
        }
        return null;
    }

    /**
     * @category 按照文件名获取图片
     */
    public static File getPicFile(String fileName) {
        if (MobileToolKit.isSDCardEnable()) {// 检查SD卡可用
            String pathStr = Environment.getExternalStorageDirectory()
                    + "/umcall";
            File root = new File(pathStr);
            if (checkDir(root, true)) {
                String subPathStr = root.getPath() + "/um_temp";
                File path = new File(subPathStr);
                if (checkDir(path, true)) {
                    File file = new File(path.getPath() + "/" + fileName);
                    if (file.exists()) {
                        return file;
                    } else {
                        try {
                            return file.createNewFile() ? file : null;
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 对拍照的照片进行重命名,格式md5.jpg
     *
     * @param oldPath
     * @param md5
     * @return
     */
    public static String renameCameraPic(String oldPath, String md5) {
        if (StringToolKit.notBlank(oldPath)) {
            File f = new File(oldPath);
            String newPath = oldPath.substring(0,
                    oldPath.lastIndexOf(File.separator) + 1)
                    + md5 + ".jpg";
            f.renameTo(new File(newPath));
            return newPath;
        }
        return "";
    }

    /**
     * 传入文件路径获取bm
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapByFilePath(String filePath) {
        if (MobileToolKit.isSDCardEnable()) {// 检查SD卡可用
            File file = new File(filePath);
            if (file.exists()) {
                return getImage(file.getPath());
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * @category 检查文件夹是否存在
     */
    public static boolean checkDir(File file, boolean isCreate) {
        if (file != null && file.isDirectory()) {
            return true;
        } else {
            return isCreate ? file.mkdir() : false;
        }
    }

    /**
     * @category 检查文件是否存在
     */
    public static boolean checkFile(File file) {
        if (file != null && file.isFile()) {
            return true;
        }
        return false;
    }

    public static boolean checkDirs(File file, boolean isCreate) {
        if (file != null && file.isDirectory()) {
            return true;
        } else {
            return isCreate ? file.mkdirs() : false;
        }
    }


    public static boolean isImageFile(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            // 如果文件的后缀是jpeg,jpg, png, bmp, gif
            return (lower.endsWith(".jpeg") || lower.endsWith(".jpg")
                    || lower.endsWith(".png") || lower.endsWith(".bmp") || lower
                    .endsWith(".gif"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAudioFile(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            return (lower.endsWith(".mp3") || lower.endsWith(".amr"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isVideoFile(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            return (lower.endsWith(".mp4") || lower.endsWith(".rmvb")
                    || lower.endsWith(".3gp") || lower.endsWith(".avi") || lower
                    .endsWith(".wmv"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDocumentFile(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            return (lower.endsWith(".txt") || lower.endsWith(".doc")
                    || lower.endsWith(".docx") || lower.endsWith(".xls")
                    || lower.endsWith(".xlsx") || lower.endsWith(".ppt")
                    || lower.endsWith(".pptx") || lower.endsWith(".pdf") || lower
                    .endsWith(".xml"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isApkFile(String name) {
        try {
            // 后缀
            return name.toLowerCase().endsWith(".apk") ? true : false;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getFileLength(String pathString) {
        File file = new File(pathString);
        return file.isFile() && file.exists() ? file.length() : 0;
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public static void openFile(File file, Context context) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);// 设置intent的Action属性
        String type = getMIMEType(file);// 获取文件file的MIME类型
        intent.setDataAndType(/* uri */Uri.fromFile(file), type);// 设置intent的data和Type属性
        context.startActivity(intent);// 跳转

    }

    /**
     * 根据文件后缀名获得对应的MIME类型
     *
     * @param file
     */
    private static String getMIMEType(File file) {
        String defaultType = "*/*";
        String fileName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return defaultType;
        }
        /* 获取文件的后缀名 */
        String fileSuffix = fileName.substring(dotIndex, fileName.length())
                .toLowerCase();
        if ("".equals(fileSuffix))
            return defaultType;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型
        String matchedType = fileTypeMap.get(fileSuffix);
        return matchedType == null ? defaultType : matchedType;
    }

    /**
     * 声明Map变量
     */
    private final static Map<String, String> fileTypeMap = new HashMap<String, String>(
            72);

    /**
     * 设置文件类型数据
     */
    static {
        // key:后缀名,value:MIME类型
        fileTypeMap.put(".3gp", "video/3gpp");
        fileTypeMap.put(".apk", "application/vnd.android.package-archive");
        fileTypeMap.put(".asf", "video/x-ms-asf");
        fileTypeMap.put(".avi", "video/x-msvideo");
        fileTypeMap.put(".bin", "application/octet-stream");
        fileTypeMap.put(".bmp", "image/bmp");
        fileTypeMap.put(".c", "text/plain");
        fileTypeMap.put(".class", "application/octet-stream");
        fileTypeMap.put(".conf", "text/plain");
        fileTypeMap.put(".cpp", "text/plain");
        fileTypeMap.put(".doc", "application/msword");
        fileTypeMap
                .put(".docx",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileTypeMap.put(".xls", "application/vnd.ms-excel");
        fileTypeMap
                .put(".xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileTypeMap.put(".exe", "application/octet-stream");
        fileTypeMap.put(".gif", "image/gif");
        fileTypeMap.put(".gtar", "application/x-gtar");
        fileTypeMap.put(".gz", "application/x-gzip");
        fileTypeMap.put(".h", "text/plain");
        fileTypeMap.put(".htm", "text/html");
        fileTypeMap.put(".html", "text/html");
        fileTypeMap.put(".jar", "application/java-archive");
        fileTypeMap.put(".java", "text/plain");
        fileTypeMap.put(".jpeg", "image/jpeg");
        fileTypeMap.put(".jpg", "image/jpeg");
        fileTypeMap.put(".js", "application/x-javascript");
        fileTypeMap.put(".log", "text/plain");
        fileTypeMap.put(".m3u", "audio/x-mpegurl");
        fileTypeMap.put(".m4a", "audio/mp4a-latm");
        fileTypeMap.put(".m4b", "audio/mp4a-latm");
        fileTypeMap.put(".m4p", "audio/mp4a-latm");
        fileTypeMap.put(".m4u", "video/vnd.mpegurl");
        fileTypeMap.put(".m4v", "video/x-m4v");
        fileTypeMap.put(".mov", "video/quicktime");
        fileTypeMap.put(".mp2", "audio/x-mpeg");
        fileTypeMap.put(".mp3", "audio/x-mpeg");
        fileTypeMap.put(".mp4", "video/mp4");
        fileTypeMap.put(".mpc", "application/vnd.mpohun.certificate");
        fileTypeMap.put(".mpe", "video/mpeg");
        fileTypeMap.put(".mpeg", "video/mpeg");
        fileTypeMap.put(".mpg", "video/mpeg");
        fileTypeMap.put(".mpg4", "video/mp4");
        fileTypeMap.put(".mpga", "audio/mpeg");
        fileTypeMap.put(".msg", "application/vnd.ms-outlook");
        fileTypeMap.put(".ogg", "audio/ogg");
        fileTypeMap.put(".pdf", "application/pdf");
        fileTypeMap.put(".png", "image/png");
        fileTypeMap.put(".pps", "application/vnd.ms-powerpoint");
        fileTypeMap.put(".ppt", "application/vnd.ms-powerpoint");
        fileTypeMap
                .put(".pptx",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        fileTypeMap.put(".prop", "text/plain");
        fileTypeMap.put(".rc", "text/plain");
        fileTypeMap.put(".rmvb", "audio/x-pn-realaudio");
        fileTypeMap.put(".rtf", "application/rtf");
        fileTypeMap.put(".sh", "text/plain");
        fileTypeMap.put(".tar", "application/x-tar");
        fileTypeMap.put(".tgz", "application/x-compressed");
        fileTypeMap.put(".txt", "text/plain");
        fileTypeMap.put(".wav", "audio/x-wav");
        fileTypeMap.put(".wma", "audio/x-ms-wma");
        fileTypeMap.put(".wmv", "audio/x-ms-wmv");
        fileTypeMap.put(".wps", "application/vnd.ms-works");
        fileTypeMap.put(".xml", "text/plain");
        fileTypeMap.put(".z", "application/x-compress");
        fileTypeMap.put(".zip", "application/x-zip-compressed");
        fileTypeMap.put("", "*/*");
    }

    /**
     * 将聊天信息写进sd卡
     *
     * @param umid
     * @param outsString
     */
    public static void writeLogToFile(String umid, String orgid,
                                      String fileName, String outsString) {

        if (MobileToolKit.isSDCardEnable()) {// 检查SD卡可用
            String pathStr = Environment.getExternalStorageDirectory()
                    + "/umcall";
            File root = new File(pathStr);
            if (checkDir(root, true)) {
                String subPathStr = root.getPath() + File.separator + fileName;
                File path = new File(subPathStr);
                if (checkDir(path, true)) {
                    File file = new File(new StringBuilder(path.getPath())
                            .append(File.separator)
                            .append(umid + "_" + orgid + ".txt").toString());
                    if (!file.exists()) {
                        try {
                            if (!file.createNewFile()) {
                                //文件创建失败
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // 两个参数，第一个是写入的文件，第二个是指是覆盖还是追加，
                    // 默认是覆盖的，就是不写第二个参数，这里设置为true就是说不覆盖，是在后面追加。
                    try {
                        FileOutputStream outputStream = new FileOutputStream(
                                file, true);
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                outputStream);
                        String timeStr = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss").format(new Date(System
                                .currentTimeMillis()));
                        // 需要写出的内容
                        outsString = outsString + ",time:" + timeStr + "\r\n";
                        bufferedOutputStream.write(outsString.getBytes());
                        // bufferedOutputStream.write(Byte.parseByte(outsString));
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取当前存储的剩余空间
     *
     * @return
     */
    public static long curStorageAvailableSize() {
        if (MobileToolKit.isSDCardEnable()) {
            return getAvailableExternalMemorySize();
        }
        return getAvailableInternalMemorySize();
    }

    /**
     * 获取sd card剩余存储空间
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (MobileToolKit.isSDCardEnable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        } else {
            return -1;
        }
    }

    /**
     * 获取sd card总的存储空间
     *
     * @return
     */
    public static long getTotalExternalMemorySize() {
        if (MobileToolKit.isSDCardEnable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            return stat.getBlockCountLong() * stat.getBlockSizeLong();
        } else {
            return -1;
        }
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getBlockCountLong() * stat.getBlockSizeLong();
    }

    /**
     * 是否有足够空间来存储下载文件
     *
     * @param download_bytes
     * @return
     */
    public static boolean isMemorySizeAvailable(long download_bytes) {

        boolean isMemoryAvailable = false;
        long freeSpace = 0;

        if (MobileToolKit.isSDCardEnable()) {
            try {
                StatFs stat = new StatFs(Environment
                        .getExternalStorageDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks()
                        * (long) stat.getBlockSize();
                if (freeSpace > download_bytes) {
                    isMemoryAvailable = true;
                } else {
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                isMemoryAvailable = false;
            }
        } else {
            try {
                StatFs stat = new StatFs(Environment.getDataDirectory()
                        .getPath());
                freeSpace = (long) stat.getAvailableBlocks()
                        * (long) stat.getBlockSize();
                if (freeSpace > download_bytes) {
                    isMemoryAvailable = true;
                } else {
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                isMemoryAvailable = false;
            }
        }
        return isMemoryAvailable;
    }

}
