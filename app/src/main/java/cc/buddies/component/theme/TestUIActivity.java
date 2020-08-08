package cc.buddies.component.theme;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.buddies.component.R;
import cc.buddies.component.common.utils.ToastUtils;

public class TestUIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ui);

        Toolbar toolbar = findViewById(R.id.title_bar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;

        // 隐藏原本Title(使用自定义居中的TextView)
        // getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 显示导航按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.aaaa).setOnClickListener(v -> ToastUtils.shortToast(v.getContext(), "111"));
    }
}
