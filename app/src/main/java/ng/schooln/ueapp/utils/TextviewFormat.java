package ng.schooln.ueapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by xyjoe on 1/8/18.
 */

public class TextviewFormat extends android.support.v7.widget.AppCompatTextView {
    public TextviewFormat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextviewFormat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextviewFormat(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),  "fonts/Lato-Regular.ttf");
        setTypeface(tf ,1);

    }
}
