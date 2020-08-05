package cc.buddies.component.test4.adapter;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cc.buddies.component.R;

/**
 * 帧列表适配器
 */
public class VideoFramesAdapter extends RecyclerView.Adapter<VideoFramesAdapter.VideoFramesViewHolder> {

    private List<Bitmap> mData;
    private int width;
    private int height;

    // 列表索引对应时间点
    private SparseArray<Long> timeSparseArray = new SparseArray<>();
    // 选中索引
    private int checkedPosition = -1;

    public VideoFramesAdapter(final List<Bitmap> data) {
        this.mData = data;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @NonNull
    @Override
    public VideoFramesViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_test_video_frames_item, parent, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        inflate.setLayoutParams(layoutParams);
        return new VideoFramesViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoFramesViewHolder holder, final int position) {
        Bitmap item = getItem(position);
        if (item == null) return;

        holder.imageView.setImageBitmap(item);
        holder.imageCheckedView.setVisibility(checkedPosition == position ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (checkedPosition == position) {
                checkedPosition = -1;
                notifyItemChanged(position);
            } else {
                int oldCheckedPosition = checkedPosition;
                checkedPosition = position;
                notifyItemChanged(oldCheckedPosition);
                notifyItemChanged(checkedPosition);
            }
        });
    }

    private Bitmap getItem(int position) {
        if (mData == null) return null;
        if (position < 0 || position > mData.size() - 1) return null;
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void addItem(Bitmap bitmap, long time) {
        if (mData != null && bitmap != null) {
            mData.add(bitmap);

            if (timeSparseArray != null) {
                timeSparseArray.append(mData.size() - 1, time);
            }
        }
    }

    /**
     * 获取选中项代表时间点
     *
     * @return -1 代表没有选择时间
     */
    public long getCheckedTime() {
        if (checkedPosition < 0 || mData == null || mData.size() - 1 < checkedPosition) return -1L;
        return timeSparseArray.get(checkedPosition, -1L);
    }

    static class VideoFramesViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView imageCheckedView;

        public VideoFramesViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.image_view);
            this.imageCheckedView = itemView.findViewById(R.id.check_image_view);
        }

    }

}
