package ng.codehaven.eko.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import ng.codehaven.eko.helpers.CustomFontHelper;

/**
 * Created by Thompson on 19/01/2015.
 */
public abstract class NomalCustomText extends TextView {
    public NomalCustomText(Context context) {
        super(context);
    }
    public NomalCustomText(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public NomalCustomText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
