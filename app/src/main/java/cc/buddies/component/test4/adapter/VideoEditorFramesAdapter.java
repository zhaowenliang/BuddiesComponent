package cc.buddies.component.test4.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoEditorFramesAdapter extends RecyclerView.Adapter<VideoEditorFramesAdapter.VideoEditorViewHolder> {

    private List<Bitmap> bitmaps;
    private int itemWidth;

    public VideoEditorFramesAdapter(final List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public void setItemWidth(final int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @NonNull
    @Override
    public VideoEditorViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);

        return new VideoEditorViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoEditorViewHolder holder, final int position) {
        Bitmap bitmap = getItem(position);
        holder.imageView.getLayoutParams().width = this.itemWidth;

        holder.imageView.setImageBitmap(bitmap);
    }

    public void addItem(Bitmap bitmap) {
        if (bitmaps == null || bitmap == null) return;

        this.bitmaps.add(bitmap);
        this.notifyItemInserted(getItemCount() - 1);
    }

    public Bitmap getItem(int position) {
        if (this.bitmaps == null) return null;
        return this.bitmaps.get(position);
    }

    @Override
    public int getItemCount() {
        return this.bitmaps == null ? 0 : this.bitmaps.size();
    }

    static class VideoEditorViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public VideoEditorViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }

}
