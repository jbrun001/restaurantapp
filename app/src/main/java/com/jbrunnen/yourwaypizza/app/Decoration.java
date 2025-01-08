package com.jbrunnen.yourwaypizza.app;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/* this is a class that helps to style all the recyclers, it is a class because it is used 3 times
    in the solution, but in different activties.
 */
public class Decoration extends RecyclerView.ItemDecoration {
    private final int space;

    public Decoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        if (parent.getChildAdapterPosition(view) == 0 | parent.getChildAdapterPosition(view)
                == 1) {
            outRect.top = space;
        }
    }
}