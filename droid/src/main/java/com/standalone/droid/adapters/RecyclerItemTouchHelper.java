package com.standalone.droid.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.R;

public abstract class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private final Context context;

    public RecyclerItemTouchHelper(Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAbsoluteAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            onSwipeLeft(position);
        } else {
            onSwipeRight(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        // Initialize icon and background
        if (dX > 0) {
            // Swiping right
            icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.colorDanger));
        } else {
            // Swiping left
            icon = ContextCompat.getDrawable(context, R.drawable.ic_edit);
            background = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

        // align icon
        assert icon != null;
        int icMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int icTop = itemView.getTop() + icMargin;
        int icBottom = icTop + icon.getIntrinsicHeight();

        if (dX > 0) {// Swiping to the right
            int icLeft = itemView.getLeft() + icMargin;
            int icRight = icLeft + icon.getIntrinsicWidth();
            icon.setBounds(icLeft, icTop, icRight, icBottom);

            int bgRight = itemView.getLeft() + ((int) dX) + backgroundCornerOffset;

            background.setBounds(itemView.getLeft(), itemView.getTop(), bgRight, itemView.getBottom());

        } else if (dX < 0) { // Swiping to the left
            int icRight = itemView.getRight() - icMargin;
            int icLeft = icRight - icon.getIntrinsicWidth();
            icon.setBounds(icLeft, icTop, icRight, icBottom);

            int bgLeft = itemView.getRight() + ((int) dX) - backgroundCornerOffset;
            background.setBounds(bgLeft, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {// view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

    public abstract void onSwipeLeft(int position);

    public abstract void onSwipeRight(int position);
}
