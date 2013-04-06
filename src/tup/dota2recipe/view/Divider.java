package tup.dota2recipe.view;

import tup.dota2recipe.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

public class Divider extends View {
    public Divider(Context context) {
        super(context);
        init(context, null, 0);
    }

    public Divider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Divider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * The top margin in pixels of the child.
     */
    public int topMargin;

    /**
     * The bottom margin in pixels of the child.
     */
    public int bottomMargin;

    @SuppressLint("InlinedApi")
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Divider, defStyle, 0);

        final boolean vertical = a.getInt(R.styleable.Divider_android_orientation,
                android.widget.LinearLayout.VERTICAL) == android.widget.LinearLayout.VERTICAL;
        this.topMargin = a.getDimensionPixelSize(R.styleable.Divider_marginTop, 6);
        this.bottomMargin = a.getDimensionPixelSize(R.styleable.Divider_marginBottom, 6);

        a.recycle();
        
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(
                vertical ? R.attr.dividerVertical : R.attr.dividerHorizontal,
                value, true);
        if (value.resourceId > 0) {
            setBackgroundResource(value.resourceId);
        }

        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, topMargin, 0, bottomMargin);
        this.setLayoutParams(params);
    }
}
