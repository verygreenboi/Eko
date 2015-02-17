package ng.codehaven.eko.ui.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ng.codehaven.eko.R;
import ng.codehaven.eko.utils.UIUtils;

/**
 * Created by Thompson on 06/02/2015.
 */
public class BusinessContextMenu extends LinearLayout {

    protected static final int CONTEXT_MENU_WIDTH = UIUtils.dpToPx(128);

    private int mItem = -1;

    private OnBusinessContextMenuItemClickListener onContextMenuItemClickListener;

    public BusinessContextMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(int mItem){
        this.mItem = mItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    public void dismiss(){
        ((ViewGroup)getParent()).removeView(BusinessContextMenu.this);
    }

    @OnClick(R.id.btnDelete)
    public void onDeleteClick(){
        if (onContextMenuItemClickListener != null){
            onContextMenuItemClickListener.onDeleteItem(mItem);
        }
    }

    public void setOnBusinessContextMenuItemClickListener(OnBusinessContextMenuItemClickListener onContextMenuItemClickListener) {
        this.onContextMenuItemClickListener = onContextMenuItemClickListener;
    }

    public interface OnBusinessContextMenuItemClickListener {
        public void onDeleteItem(int mItem);
    }

}
