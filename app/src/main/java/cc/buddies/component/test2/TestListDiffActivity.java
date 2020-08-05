package cc.buddies.component.test2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cc.buddies.component.R;

public class TestListDiffActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TestListAdapter mListAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_diff_list);
        setTitle("列表差分数据更新");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        List<UserBean> data = getData1();
        mListAdapter = new TestListAdapter();
        mListAdapter.submitList(data);

        mRecyclerView.setAdapter(mListAdapter);
    }

    private List<UserBean> getData1() {
        List<UserBean> userBeans = new ArrayList<>();

        userBeans.add(new UserBean(1, "zhao", 10));
        userBeans.add(new UserBean(2, "wen", 11));
        userBeans.add(new UserBean(3, "liang", 12));

        return userBeans;
    }

    private List<UserBean> getData2() {
        List<UserBean> userBeans = new ArrayList<>();

        userBeans.add(new UserBean(11, "可以发现，控制等待动画的可能是Controller，也可能是Model。并且Model传递数据时，可能传递给Controller，也可能直接传递给View。把Controller的角色无限弱化，就是人人学写代码时就会使用的MVC架构了。", 10));
        userBeans.add(new UserBean(111, "可以看到，实际上，DiffUtil 的算法把效率问题解决的非常的好。在开启计算移动的情况下，1000 条数据中有 200 个修改，平均值也只有 13.54 ms ，基本上都是毫秒级的。", 10));
        userBeans.add(new UserBean(1111, "Google 官方同时也指出，如果是对大数据集的比对，最好是方在子线程中去完成计算，也就是其实是存在堵塞 UI 的情况的。所以如果你遇见了使用 DiffUtil 之后，每次刷新有卡顿的情况，可以考虑是否数据集太大，是否应该在子线程中完成计算。", 10));

        userBeans.add(new UserBean(1, "zhao", 10));
        userBeans.add(new UserBean(2, "wen", 11));
        userBeans.add(new UserBean(22, "wennnnnnnnnnnn", 11));
        userBeans.add(new UserBean(3, "liang", 12));

        return userBeans;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diff_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_update) {
            List<UserBean> data2 = getData2();
            mListAdapter.submitList(data2);
            return true;

        } else if (item.getItemId() == R.id.action_reset) {
            List<UserBean> data1 = getData1();
            mListAdapter.submitList(data1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
