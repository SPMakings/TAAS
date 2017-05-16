package com.spm.taas.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Saikat Pakira on 9/28/2016.
 */
public class TextViewIkarosRegular extends TextView {

    public TextViewIkarosRegular(Context context) {
        super(context);
        init(context);
    }

    public TextViewIkarosRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public TextViewIkarosRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        super.setTypeface(FontCache.get("Lato-Regular.ttf", context));
    }
}