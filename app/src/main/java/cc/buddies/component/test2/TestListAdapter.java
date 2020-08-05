package cc.buddies.component.test2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cc.buddies.component.R;

/**
 * Test列表适配器。
 * 继承自{@link ListAdapter}，内部带有DiffUtil差分对比功能。
 */
public class TestListAdapter extends ListAdapter<UserBean, TestListAdapter.TestListViewHolder> {

    public TestListAdapter() {
        super(new TestListItemCallback());
    }

    @NonNull
    @Override
    public TestListViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_test_diff_list_item, parent, false);
        return new TestListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final TestListViewHolder holder, final int position) {
        UserBean item = getItem(position);
        if (item == null) return;

        holder.textId.setText(String.valueOf(item.getId()));
        holder.textName.setText(item.getName());
        holder.textAge.setText(String.valueOf(item.getAge()));
    }

    // 数据变更
    @Override
    public void onCurrentListChanged(@NonNull final List<UserBean> previousList, @NonNull final List<UserBean> currentList) {
        super.onCurrentListChanged(previousList, currentList);
    }

    // ViewHolder
    static class TestListViewHolder extends RecyclerView.ViewHolder {
        TextView textId, textName, textAge;

        TestListViewHolder(@NonNull final View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.tv_id);
            textName = itemView.findViewById(R.id.tv_name);
            textAge = itemView.findViewById(R.id.tv_age);
        }
    }

    // 差分对比
    static class TestListItemCallback extends DiffUtil.ItemCallback<UserBean> {

        @Override
        public boolean areItemsTheSame(@NonNull final UserBean oldItem, @NonNull final UserBean newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull final UserBean oldItem, @NonNull final UserBean newItem) {
            if (oldItem.getId() != oldItem.getId()) return false;
            if (!TextUtils.equals(oldItem.getName(), newItem.getName())) return false;
            if (oldItem.getAge() != newItem.getAge()) return false;
            // 默认true
            return true;
        }
    }

}
