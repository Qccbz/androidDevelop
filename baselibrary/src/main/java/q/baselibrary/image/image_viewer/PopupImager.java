package q.baselibrary.image.image_viewer;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import q.baselibrary.R;
import q.baselibrary.utils.QScreen;
import q.baselibrary.utils.QUI;

public class PopupImager {

    private Context context;
    private PopupWindow menu;
    public List<ItemData> data;
    private ListView list;
    private MenuAdapter ma;
    public static final int XOFF = 0;
    public static final int YOFF = 8;

    public PopupImager(Context context, List<ItemData> data) {
        this.context = context;
        this.data = data;
        init();
    }

    private void init() {
        LayoutInflater layout = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View member = layout.inflate(R.layout.popupmenu_imager, null);

        menu = new PopupWindow(context);
        menu.setContentView(member);

        int w = QScreen.dip2px(context, 165);
        menu.setWidth(w);

        int d = QScreen.dip2px(context, 45);
        menu.setHeight(data.size() * d);
        menu.setBackgroundDrawable(new BitmapDrawable());

        menu.setAnimationStyle(-1);
        menu.setFocusable(true);
        ma = new MenuAdapter(context, data);

        list = (ListView) member.findViewById(R.id.list);
        list.setAdapter(ma);
    }

    public void showAtLocation(View v, int gravity, int xoff, int yoff) {
        menu.showAtLocation(v, gravity, xoff, yoff);
    }

    public void setOnItemClickListener(OnItemClickListener oicl) {
        list.setOnItemClickListener(oicl);
    }

    public void dismiss() {
        menu.dismiss();
    }

    public void setWidth(int w) {
        int width = QScreen.dip2px(context, w);
        menu.setWidth(width);
    }

    public void setHeight(int h) {
        int height = QScreen.dip2px(context, h);
        menu.setHeight((data.size() + 1) * height);
    }

    private class MenuAdapter extends BaseAdapter {

        private Context context;
        private List<ItemData> data;

        public MenuAdapter(Context context, List<ItemData> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.popupmenu_imager_item, null);
            }

            ItemData mb = data.get(position);
            ItemView mi = new ItemView(convertView);
            if (mb.isIconshow()) {
                mi.setIcon(mb.getIcon());
                mi.icon.setVisibility(View.VISIBLE);
            } else {
                mi.icon.setVisibility(View.GONE);
            }
            mi.setTitle(mb.getTitle());
            return convertView;
        }
    }

    public static class ItemData {
        private int icon;
        private String title;
        private boolean isIconshow = true;
        /**
         * 各种对应的类型 企业版添加 1保存到本地 2邀请好友 3发送名片
         **/
        private int type;

        public ItemData(int icon, String title) {
            setIcon(icon);
            setTitle(title);
        }

        public ItemData(int icon, String title, int type) {
            setIcon(icon);
            setTitle(title);
            setType(type);
        }

        public ItemData(String title) {
            super();
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isIconshow() {
            return isIconshow;
        }

        public void setIconshow(boolean isIconshow) {
            this.isIconshow = isIconshow;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private class ItemView {

        private TextView icon, title;

        public ItemView(View v) {
            icon = (TextView) v.findViewById(R.id.icon);
            title = (TextView) v.findViewById(R.id.title);
        }

        public void setIcon(int icon) {
            this.icon.setBackgroundResource(icon);
        }

        public void setTitle(String menu) {
            title.setText(menu);
        }
    }

}
