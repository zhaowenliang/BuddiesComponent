package cc.buddies.component.test3.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cc.buddies.component.R;


public class TestLoaderAdapter extends CursorAdapter<TestLoaderAdapter.TestLoaderViewHolder> {

    private ContentResolver mContentResolver;

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter;
     *                Currently it accept {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public TestLoaderAdapter(final Context context, final Cursor c, final int flags) {
        super(context, c, flags);
        this.mContentResolver = context.getContentResolver();
    }

    @NonNull
    @Override
    public TestLoaderViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_test_loader_list_item, parent, false);
        return new TestLoaderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final TestLoaderViewHolder holder, @NonNull final Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE));
        String phone = "";

        // 取得电话号码
        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        try (Cursor cursorPhone = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null)) {
            if (cursorPhone != null && cursorPhone.moveToFirst()) {
                phone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.textId.setText(id);
        holder.textName.setText(name);
        holder.textPhone.setText(phone);
    }

    @Override
    protected void onContentChanged() {
        Log.d("aaaa", "TestLoaderAdapter onContentChanged()");
    }

    static class TestLoaderViewHolder extends RecyclerView.ViewHolder {

        TextView textId;
        TextView textName;
        TextView textPhone;

        public TestLoaderViewHolder(@NonNull final View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.tv_id);
            textName = itemView.findViewById(R.id.tv_name);
            textPhone = itemView.findViewById(R.id.tv_phone);
        }
    }

}
