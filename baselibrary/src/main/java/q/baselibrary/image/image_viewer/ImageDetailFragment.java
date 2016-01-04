package q.baselibrary.image.image_viewer;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import q.baselibrary.R;
import q.baselibrary.image.image_viewer.PopupImager.ItemData;
import q.baselibrary.image.image_viewer.photoview.PhotoViewAttacher;
import q.baselibrary.utils.FileUtils;
import q.baselibrary.utils.UIToolKit;

/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends Fragment {

    private String mImageUrl;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });
        mAttacher.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showPop(v);
                return true;
            }
        });
        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 异步加载图片
        new ImageTask().execute(mImageUrl);
    }

    private class ImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //get image from server or local
            return FileUtils.getImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            progressBar.setVisibility(View.GONE);
            if (bm != null) {
                mImageView.setImageBitmap(bm);
                mAttacher.update();
            }
        }
    }

    private PopupImager popupMenu;

    private void showPop(View v) {

        // data
        List<ItemData> data = new ArrayList<ItemData>();
        ItemData item1 = new ItemData("保存到本地相册");
        PopupImager.ItemData item2 = new PopupImager.ItemData("取消");
        data.add(item1);
        data.add(item2);

        popupMenu = new PopupImager(getActivity(), data);
        popupMenu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                popupMenu.dismiss();
                switch (pos) {
                    case 0:
                        // 保存图片到本地相册
                        try {
                            File f = new File(mImageUrl);
                            if (f != null && f.isFile()) {
                                MediaStore.Images.Media.insertImage(getActivity()
                                        .getContentResolver(), mImageUrl, f
                                        .getName(), null);
                                UIToolKit.showToastShort(getActivity(), "保存成功");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            UIToolKit.showToastShort(getActivity(), "保存失败");
                        }
                        break;
                }
            }
        });
        popupMenu.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

}
