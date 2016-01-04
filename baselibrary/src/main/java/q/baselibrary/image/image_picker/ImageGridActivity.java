package q.baselibrary.image.image_picker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.List;

import q.baselibrary.R;
import q.baselibrary.base.BaseActivity;
import q.baselibrary.panel.TitlePanel;
import q.baselibrary.utils.UIToolKit;


public class ImageGridActivity extends BaseActivity {

    public static final String EXTRA_IMAGE_LIST = "imagelist";
    private List<ImageItem> dataList;
    private GridView gridView;
    private ImageGridAdapter adapter;
    private AlbumHelper helper;
    private TitlePanel tp;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    UIToolKit.showToastShort(context, "最多选择"
                            + ImagePickerMain.MAX_NUM + "张图片");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_picker_grid);

        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());

        dataList = (List<ImageItem>) getIntent().getSerializableExtra(
                EXTRA_IMAGE_LIST);

        initView();

    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(ImageGridActivity.this, dataList, mHandler);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
            }
        });
        tp = new TitlePanel(this);
        tp.setTitle("选择图片");
        tp.setRightTitle("完成");
        tp.rightTip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
