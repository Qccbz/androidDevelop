package q.baselibrary.image.image_picker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import q.baselibrary.R;
import q.baselibrary.base.BaseActivity;
import q.baselibrary.panel.TitlePanel;


public class ImagePickerMain extends BaseActivity {

    private List<ImageBucket> dataList;
    private GridView gridView;
    private ImageBucketAdapter adapter;
    private AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap bimap;
    public static Queue<String> selUrls = new LinkedList<String>();
    public static final int MAX_NUM = 5;
    private TitlePanel tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_bucket);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        initData();
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (selUrls != null) {
            selUrls.clear();
            selUrls = null;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        dataList = helper.getImagesBucketList(false);
        bimap = BitmapFactory.decodeResource(getResources(), R.mipmap.default_img);
    }

    /**
     * 初始化view视图
     */
    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new ImageBucketAdapter(ImagePickerMain.this, dataList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ImagePickerMain.this, ImageGridActivity.class);
                intent.putExtra(ImagePickerMain.EXTRA_IMAGE_LIST, (Serializable) dataList.get(position).imageList);
                startActivity(intent);
            }
        });
        tp = new TitlePanel(this);
        tp.setTitle("相册");
        tp.setRightTitle("完成");
        tp.rightTip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                try {
                    if (selUrls != null) {
                        int len = selUrls.size();
                        if (len > 0) {
                            String[] urls = new String[len];
                            for (int i = 0; i < len; i++) {
                                urls[i] = selUrls.remove();
                            }
                            data.putExtra(EXTRA_IMAGE_LIST, urls);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }

}
