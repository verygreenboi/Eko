package ng.codehaven.eko.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import ng.codehaven.eko.utils.CustomFontHelper;

/**
 * Created by mrsmith on 11/19/14.
 * Fontified
 */
public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
    }
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
