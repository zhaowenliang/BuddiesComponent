package cc.buddies.component;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cc.buddies.component.test0.LiveDataActivity;
import cc.buddies.component.test1.AActivity;
import cc.buddies.component.test2.TestListDiffActivity;
import cc.buddies.component.test3.TestLoaderActivity;
import cc.buddies.component.test4.VideoEditorActivity;
import cc.buddies.component.test4.VideoFrames2Activity;
import cc.buddies.component.theme.TestUIActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                finishApp();
//            }
//        });
    }

    public void onClickTest(View view) {
        Toast.makeText(this, "no test", Toast.LENGTH_SHORT).show();
    }

    public void onClickLiveData(View view) {
        startActivity(new Intent(this, LiveDataActivity.class));
    }

    public void onClickTest1(View view) {
        startActivity(new Intent(this, AActivity.class));
    }

    public void onClickTest2(View view) {
        startActivity(new Intent(this, TestListDiffActivity.class));
    }

    public void onClickTest3(View view) {
        startActivity(new Intent(this, TestLoaderActivity.class));
    }

    public void onClickTest4(View view) {
        startActivity(new Intent(this, VideoFrames2Activity.class));
    }

    public void onClickTest5(View view) {
        startActivity(new Intent(this, VideoEditorActivity.class));
    }

    public void onClickTestUI(View view) {
        startActivity(new Intent(this, TestUIActivity.class));
    }

}
