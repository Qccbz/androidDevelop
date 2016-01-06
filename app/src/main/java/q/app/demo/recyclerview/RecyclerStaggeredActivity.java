package q.app.demo.recyclerview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import q.app.R;

public class RecyclerStaggeredActivity extends ActionBarActivity {

    @InjectView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;
    private List<String> mDatas;
    private RecyclerStaggeredAdapter mRecyclerStaggeredAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_recyclerview);
        ButterKnife.inject(this);
        initData();

        mRecyclerStaggeredAdapter = new RecyclerStaggeredAdapter(this, mDatas);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mRecyclerStaggeredAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置item动画
        initEvent();
    }

    private void initEvent() {
        mRecyclerStaggeredAdapter.setOnItemClickLitener(new RecyclerStaggeredAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerStaggeredActivity.this,
                        position + " click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(RecyclerStaggeredActivity.this,
                        position + " long click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recycler_staggered, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_add:
                mRecyclerStaggeredAdapter.addData(1);
                break;
            case R.id.id_action_delete:
                mRecyclerStaggeredAdapter.removeData(1);
                break;
        }
        return true;
    }

}
