package q.baselibrary.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 处理图片的工具类，目前没用到
 * 
 * @author www
 * 
 */
public class PictureToolKit {

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

	/** @category 保存图片 */
	public static void saveBitmap(File file, Bitmap mBitmap) {
		saveBitmap(file, mBitmap, 30);
	}

	/** @category 保存头像 */
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

	/** @category 保存图片 */
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
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}
	
	// 缩放图片
	public static Bitmap zoomImg(Context context, Bitmap bm) {
		float maxPixel = UIToolKit.dip2px(context, 80);
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

	/** @category 压缩图片 */
	public static Bitmap imageZoom(Bitmap bitMap) {
		Bitmap temp = bitMap;
		// 图片允许最大空间 单位：KB
		double maxSize = 30.00;
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		byte[] b = bitmap2Bytes(temp);
		// 将字节换成KB
		double mid = b.length / 1024;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			temp = zoomImage(temp, temp.getWidth() / Math.sqrt(i),
					temp.getHeight() / Math.sqrt(i));
		}
		return temp;
	}

	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
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
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	/** @category 将byte[]转换成bitmap */
	public static Bitmap byte2Bitmap(byte[] data) {
		int dataLen = data == null ? 0 : data.length;
		return dataLen > 0 ? BitmapFactory.decodeByteArray(data, 0, dataLen)
				: null;
	}

	/** @category bitmap转成Drawable */
	public static Drawable bitmap2Drawable(Activity act, Bitmap bm) {
		DisplayMetrics metrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Resources r = new Resources(act.getAssets(), metrics, null);
		Drawable bgDrawble2 = new BitmapDrawable(r, bm);
		return bgDrawble2;
	}

	/** @category bitmap转成Drawable */
	public static Drawable bitmap2Drawable(Bitmap bm) {
		return new BitmapDrawable(bm);
	}

	/** @category drawable转成bitmap */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	/** @category bitmap转成byte[] */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
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
			Log.v("myLog", "splite bitmap err: " + e.toString());
			e.printStackTrace();
		}

		return result;
	}
	
	public static void reNamePic(String path, String newName) {
		if (StringToolKit.notBlank(path)) {
			File f = new File(path);
			if (f.isFile()) {
				f.renameTo(new File(newName));
			}
		}
	}
	
}
