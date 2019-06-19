package com.lte.lte;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;

// 사진 등록 시스템 중 이미지 선택
// 작성자 : 배경률

public class CheckableImage extends RelativeLayout implements Checkable {
    public CheckableImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.item_cb);
        if (cb.isChecked() != checked)
            cb.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.item_cb);
        return cb.isChecked();
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.item_cb);
        setChecked(!cb.isChecked());
    }
}
