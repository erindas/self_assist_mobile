package com.aa.personal_assist_widget;

/**
 * Created by kummukes on 4/15/2018.
 */

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Item decoration which draws drop shadow of each item views.
 */
public class ItemShadowDecorator extends RecyclerView.ItemDecoration {
    private final NinePatchDrawable mShadowDrawable;
    private final Rect mShadowPadding = new Rect();
    private final boolean mCastShadowForTransparentBackgroundItem;

    /**
     * Constructor.
     *
     * @param shadow 9-patch drawable used for drop shadow
     */
    public ItemShadowDecorator(@NonNull NinePatchDrawable shadow) {
        this(shadow, true);
    }

    /**
     * Constructor.
     *
     * @param shadow 9-patch drawable used for drop shadow
     * @param castShadowForTransparentBackgroundItem Whether to cast shadows for transparent items
     */
    public ItemShadowDecorator(@NonNull NinePatchDrawable shadow, boolean castShadowForTransparentBackgroundItem) {
        mShadowDrawable = shadow;
        mShadowDrawable.getPadding(mShadowPadding);
        mCastShadowForTransparentBackgroundItem = castShadowForTransparentBackgroundItem;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int childCount = parent.getChildCount();

        if (childCount == 0) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            /*if (!shouldDrawDropShadow(child)) {
                Log.i("ANUNYAAAAAAAAAAAAAAA", "returned false");
                continue;
            }*/

            final int tx = (int) (child.getTranslationX() + 2f);
            final int ty = (int) (child.getTranslationY() + 2f);

            final int left = child.getLeft() - mShadowPadding.left;
            final int right = child.getRight() + mShadowPadding.right;
            final int top = child.getTop() - mShadowPadding.top;
            final int bottom = child.getBottom() + mShadowPadding.bottom;

            mShadowDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty);
            mShadowDrawable.draw(c);
        }
    }

    private boolean shouldDrawDropShadow(View child) {
        if (child.getVisibility() != View.VISIBLE) {
            return false;
        }
        if (child.getAlpha() != 1.0f) {
            return false;
        }

        Drawable background = child.getBackground();
        if (background == null) {
            return false;
        }

        if (!mCastShadowForTransparentBackgroundItem && (background instanceof ColorDrawable)) {
            //noinspection RedundantCast
            if (((ColorDrawable) background).getAlpha() == 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, 0);
    }
}
