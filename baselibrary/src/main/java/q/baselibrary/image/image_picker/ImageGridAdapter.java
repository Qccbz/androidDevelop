package q.baselibrary.image.image_picker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import q.baselibrary.R;

public class ImageGridAdapter extends BaseAdapter {

    private Activity act;
    private List<ImageItem> data;
    private BitmapCache cache;
    private Handler mHandler;

    private BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                              Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals((String) imageView.getTag())) {
                    ((ImageView) imageView).setImageBitmap(bitmap);
                }
            }
        }
    };

    public ImageGridAdapter(Activity act, List<ImageItem> list, Handler mHandler) {
        this.act = act;
        data = list;
        cache = new BitmapCache();
        this.mHandler = mHandler;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int pos) {
        return data == null ? null : data.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private class Holder {
        private ImageView iv;
        private ImageView selected;
        private TextView text;
    }

    @Override
    public View getView(final int pos, View v, ViewGroup parent) {
        final Holder h;

        if (v == null) {
            h = new Holder();
            v = View.inflate(act, R.layout.image_picker_grid_item, null);
            h.iv = (ImageView) v.findViewById(R.id.image);
            h.selected = (ImageView) v.findViewById(R.id.isselected);
            h.text = (TextView) v.findViewById(R.id.item_image_grid_text);
            v.setTag(h);
        } else {
            h = (Holder) v.getTag();
        }

        final ImageItem item = data.get(pos);
        h.iv.setTag(item.imagePath);
        cache.displayBmp(h.iv, item.thumbnailPath, item.imagePath, callback);
        if (item.isSelected || isSelect(item)) {
            h.selected.setImageResource(R.mipmap.btn_image_s_9);
            h.text.setBackgroundResource(R.drawable.image_picker_line);
        } else {
            h.selected.setImageResource(-1);
            h.text.setBackgroundColor(0x00000000);
        }
        h.iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ImagePickerMain.selUrls == null) {
                    ImagePickerMain.selUrls = new LinkedList<String>();
                }

                String path = item.imagePath;
                if (ImagePickerMain.selUrls.size() < ImagePickerMain.MAX_NUM) {
                    item.isSelected = !item.isSelected;
                    if (item.isSelected) {
                        h.selected.setImageResource(R.mipmap.btn_image_s_9);
                        h.text.setBackgroundResource(R.drawable.image_picker_line);

                        putPath(path);

                    } else if (!item.isSelected) {
                        h.selected.setImageResource(-1);
                        h.text.setBackgroundColor(0x00000000);

                        removePath(path);
                    }
                } else if (ImagePickerMain.selUrls.size() == ImagePickerMain.MAX_NUM) {
                    if (item.isSelected == true) {
                        item.isSelected = !item.isSelected;
                        h.selected.setImageResource(-1);
                        removePath(path);
                    } else {
                        Message message = Message.obtain(mHandler, 0);
                        message.sendToTarget();
                    }
                }
            }
        });
        return v;
    }

    private boolean isSelect(ImageItem item) {
        if (ImagePickerMain.selUrls != null && item != null) {
            boolean isHave = ImagePickerMain.selUrls.contains(item.imagePath);
            item.isSelected = isHave;
            return isHave;
        }
        return false;
    }

    private void putPath(String path) {
        if (ImagePickerMain.selUrls != null) {
            ImagePickerMain.selUrls.add(path);
        }
    }

    private void removePath(String path) {
        if (ImagePickerMain.selUrls != null) {
            ImagePickerMain.selUrls.remove(path);
        }
    }

}
