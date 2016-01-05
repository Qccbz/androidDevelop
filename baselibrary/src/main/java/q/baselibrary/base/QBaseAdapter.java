package q.baselibrary.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class QBaseAdapter<T> extends BaseAdapter {

    protected Context context;
    protected LayoutInflater inflater;
    protected List<T> dataList = new ArrayList<T>();

    public QBaseAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    public boolean isEmpty() {
        return dataList.isEmpty();
    }


    public void addItems(List<T> itemList) {
        this.dataList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void setItems(List<T> itemList) {
        this.dataList.clear();
        this.dataList = itemList;
        notifyDataSetChanged();
    }

    public void clearItems() {
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    abstract public View getView(int i, View view, ViewGroup viewGroup);
}
