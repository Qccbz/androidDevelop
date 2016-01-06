package q.baselibrary.panel;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import q.baselibrary.R;


public class TitlePanel {

    private Activity act;
    public RelativeLayout main;
    public TextView title, leftTip, rightTip;
    public ImageView back, rightImg, leftImg;

    public TitlePanel(Activity act) {
        this.act = act;
        if (act != null) {
            init();
            setListener();
        }
    }

    private void init() {
        main = (RelativeLayout) act.findViewById(R.id.title_panel);
        back = (ImageView) act.findViewById(R.id.titleBack);
        leftTip = (TextView) act.findViewById(R.id.titleLeftTip);
        title = (TextView) act.findViewById(R.id.title);
        rightTip = (TextView) act.findViewById(R.id.titleRightTip);

        rightImg = (ImageView) act.findViewById(R.id.titleRightImg);
        leftImg = (ImageView) act.findViewById(R.id.titleLeftImg);
    }

    private void setListener() {
        back.setOnClickListener(listener);
        leftTip.setOnClickListener(listener);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setRightTitle(String title) {
        rightTip.setText(title);
        showRight(true);
    }

    public void setLeftTitle(String title) {
        leftTip.setText(title);
        showLeft(true);
    }


    public void showLeft(boolean flag) {
        int s = flag ? View.VISIBLE : View.GONE;
        leftTip.setVisibility(s);
        back.setVisibility(s);
    }

    public void showRight(boolean flag) {
        int s = flag ? View.VISIBLE : View.GONE;
        rightTip.setVisibility(s);
    }

    public void setRightImg(int resourceId) {
        rightImg.setImageResource(resourceId);
        rightImg.setVisibility(View.VISIBLE);
        showRight(false);
    }

    public void setleftImg(int resourceId) {
        leftImg.setImageResource(resourceId);
        leftImg.setVisibility(View.VISIBLE);
        showLeft(false);
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == back || v == leftTip) {
                act.onBackPressed();
            }
        }
    };
}
