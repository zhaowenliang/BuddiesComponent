package cc.buddies.component.common.recyclerview;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * RecyclerView的addOnItemTouchListener监听处理，内部增加Item点击处理事件。
 * 不能触发Item背景/前景样式，考虑解决该问题。
 */
public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private WeakReference<RecyclerView> mRecyclerViewWeakReference;
    private GestureDetectorCompat mGestureDetector;

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        this.mRecyclerViewWeakReference = new WeakReference<>(recyclerView);
        this.mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mRecyclerViewWeakReference != null && mRecyclerViewWeakReference.get() != null) {
                RecyclerView recyclerView = mRecyclerViewWeakReference.get();
                final View childViewUnder = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childViewUnder != null) {
                    final RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(childViewUnder);
                    onItemClick(childViewHolder);
                    return true;
                }
            }

            return super.onSingleTapUp(e);
        }
    }

    /**
     * 分发Item点击事件
     * @param holder ViewHolder
     */
    public abstract void onItemClick(RecyclerView.ViewHolder holder);

}
