package q.baselibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

public class QImage {

    private QImage() {
        throw new AssertionError();
    }

    /**
     * convert Bitmap to byte array
     *
     * @param b
     * @return
     */
    public static byte[] bitmapToByte(Bitmap b) {
        if (b == null) {
            return null;
        }

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * convert byte array to Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * convert Drawable to Bitmap
     *
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        return d == null ? null : ((BitmapDrawable) d).getBitmap();
    }

    /**
     * convert Bitmap to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * convert Drawable to byte array
     *
     * @param d
     * @return
     */
    public static byte[] drawableToByte(Drawable d) {
        return bitmapToByte(drawableToBitmap(d));
    }

    /**
     * convert byte array to Drawable
     *
     * @param b
     * @return
     */
    public static Drawable byteToDrawable(byte[] b) {
        return bitmapToDrawable(byteToBitmap(b));
    }

    /**
     * get input stream from network by imageurl, you need to close inputStream yourself
     *
     * @param imageUrl
     * @param readTimeOutMillis
     * @return
     * @see QImage#getInputStreamFromUrl(String, int, boolean)
     */
    public static InputStream getInputStreamFromUrl(String imageUrl, int readTimeOutMillis) {
        return getInputStreamFromUrl(imageUrl, readTimeOutMillis, null);
    }

    /**
     * get input stream from network by imageurl, you need to close inputStream yourself
     *
     * @param imageUrl
     * @param readTimeOutMillis read time out, if less than 0, not set, in mills
     * @param requestProperties http request properties
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static InputStream getInputStreamFromUrl(String imageUrl, int readTimeOutMillis,
                                                    Map<String, String> requestProperties) {
        InputStream stream = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            QHttp.setURLConnection(requestProperties, con);
            if (readTimeOutMillis > 0) {
                con.setReadTimeout(readTimeOutMillis);
            }
            stream = con.getInputStream();
        } catch (MalformedURLException e) {
            QIO.close(stream);
            throw new RuntimeException("MalformedURLException occurred. ", e);
        } catch (IOException e) {
            QIO.close(stream);
            throw new RuntimeException("IOException occurred. ", e);
        }
        return stream;
    }

    /**
     * get drawable by imageUrl
     *
     * @param imageUrl
     * @param readTimeOutMillis
     * @return
     * @see QImage#getDrawableFromUrl(String, int, boolean)
     */
    public static Drawable getDrawableFromUrl(String imageUrl, int readTimeOutMillis) {
        return getDrawableFromUrl(imageUrl, readTimeOutMillis, null);
    }

    /**
     * get drawable by imageUrl
     *
     * @param imageUrl
     * @param readTimeOutMillis read time out, if less than 0, not set, in mills
     * @param requestProperties http request properties
     * @return
     */
    public static Drawable getDrawableFromUrl(String imageUrl, int readTimeOutMillis,
                                              Map<String, String> requestProperties) {
        InputStream stream = getInputStreamFromUrl(imageUrl, readTimeOutMillis, requestProperties);
        Drawable d = Drawable.createFromStream(stream, "src");
        QIO.close(stream);
        return d;
    }

    /**
     * get Bitmap by imageUrl
     *
     * @param imageUrl
     * @param readTimeOut
     * @return
     * @see QImage#getBitmapFromUrl(String, int, boolean)
     */
    public static Bitmap getBitmapFromUrl(String imageUrl, int readTimeOut) {
        return getBitmapFromUrl(imageUrl, readTimeOut, null);
    }

    /**
     * get Bitmap by imageUrl
     *
     * @param imageUrl
     * @param requestProperties http request properties
     * @return
     */
    public static Bitmap getBitmapFromUrl(String imageUrl, int readTimeOut, Map<String, String> requestProperties) {
        InputStream stream = getInputStreamFromUrl(imageUrl, readTimeOut, requestProperties);
        Bitmap b = BitmapFactory.decodeStream(stream);
        QIO.close(stream);
        return b;
    }

    /**
     * scale image
     *
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     *
     * @param org
     * @param scaleWidth  sacle of width
     * @param scaleHeight scale of height
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }


    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * @category 保存图片
     */
    public static void saveBitmap(File file, Bitmap mBitmap) {
        saveBitmap(file, mBitmap, 30);
    }

    /**
     * @category 保存头像
     */
    public static void saveIcon(File file, Bitmap mBitmap) {
        FileOutputStream outStream = null;
        try {
            file.createNewFile();
            outStream = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @category 保存图片
     */
    public static void saveBitmap(File file, Bitmap mBitmap, int quality) {
        BufferedOutputStream buffStream = null;
        try {
            file.createNewFile();
            buffStream = new BufferedOutputStream(new FileOutputStream(file));
            mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, buffStream);
            buffStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buffStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 缩放图片
    public static Bitmap zoomImg(Context context, Bitmap bm) {
        float maxPixel = QScreen.dip2px(context, 80);
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width >= height) {
            // 以宽为准
            float scaleW = maxPixel / width;
            float scaleH = (maxPixel * height / width) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleW, scaleH);
            bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        } else {
            // 以高为准
            float scaleW = (maxPixel * width / height) / width;
            float scaleH = maxPixel / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleW, scaleH);
            bm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }
        return bm;
    }

    /**
     * @category 压缩图片
     */
    public static Bitmap imageZoom(Bitmap bitMap) {
        Bitmap bm = bitMap;
        // 图片允许最大空间 单位：KB
        double maxSize = 30.00;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        byte[] b = bitmapToByte(bm);
        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bm = zoomImage(bm, bm.getWidth() / Math.sqrt(i), bm.getHeight() / Math.sqrt(i));
        }
        return bm;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
    }

    /**
     * 根据行列数切分图片
     */
    public static Vector<Bitmap> splitBitmap(Bitmap src, int wcount, int hcount) {
        Vector<Bitmap> result = new Vector<Bitmap>();

        try {
            if (src != null) {
                int x = 0, y = 0;
                int w = src.getWidth() / wcount, h = src.getHeight() / hcount;

                for (int i = 0; i < hcount; i++) {
                    y = i * h;
                    x = 0;

                    for (int j = 0; j < wcount; j++) {
                        result.add(Bitmap.createBitmap(src, x, y, w, h));
                        x += w;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void renamePic(String path, String newName) {
        if (!QString.isBlank(path)) {
            File f = new File(path);
            if (f.isFile()) {
                f.renameTo(new File(newName));
            }
        }
    }

    private static int getImageScale(String imagePath, boolean isMin) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the bitmap info
        // without having to allocate the memory for its pixels.
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
        }
        return scale;
    }

    private static int getImageScale(String imagePath, int width, int height) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        while (option.outWidth / scale >= width
                || option.outHeight / scale >= height) {
            scale *= 2;
        }
        return scale;
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

    /**
     * 传入文件路径获取bm
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmap(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return getImage(file.getPath());
        }
        return null;
    }
}
