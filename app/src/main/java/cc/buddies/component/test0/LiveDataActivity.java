package cc.buddies.component.test0;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import cc.buddies.component.R;
import cc.buddies.component.test0.model.Test1ViewModel;
import cc.buddies.component.test0.model.Test2ViewModel;
import cc.buddies.component.test0.model.TestViewModel;

public class LiveDataActivity extends AppCompatActivity {

    private static final String TAG = "LiveDataActivity";

    private TextView textView;
    private TextView textView1;
    private TextView textView2;

    private TestViewModel mTestViewModel;
    private Test1ViewModel mTest1ViewModel;
    private Test2ViewModel mTest2ViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livedata);
        textView = findViewById(R.id.text_view);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);

        initVariable();
        initVariable1();
        initVariable2();
    }

    private void initVariable() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: " + s);
                textView.setText(s);
            }
        };

        mTestViewModel = new TestViewModel();
        mTestViewModel.getStatus().observe(this, observer);

        mTestViewModel.getStatus().setValue("onCreate");
    }

    private void initVariable1() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged1: " + s);
                textView1.setText(s);
            }
        };

        mTest1ViewModel = new Test1ViewModel();
        mTest1ViewModel.getStrLiveData().observe(this, observer);
        mTest1ViewModel.getNumLiveData().setValue(1);
    }

    private void initVariable2() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged2: " + s);
                textView2.setText(s);
            }
        };

        mTest2ViewModel = new Test2ViewModel();
        mTest2ViewModel.getNameLiveData().observe(this, observer);
        mTest2ViewModel.getNumLiveData().setValue(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTestViewModel.getStatus().setValue("onStart");
        mTest1ViewModel.getNumLiveData().setValue(2);
        mTest2ViewModel.getNumLiveData().setValue(2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTestViewModel.getStatus().setValue("onResume");
        mTest1ViewModel.getNumLiveData().setValue(3);
        mTest2ViewModel.getNumLiveData().setValue(3);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTestViewModel.getStatus().setValue("onRestart");
        mTest1ViewModel.getNumLiveData().setValue(4);
        mTest2ViewModel.getNumLiveData().setValue(4);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTestViewModel.getStatus().setValue("onPause");
        mTest1ViewModel.getNumLiveData().setValue(5);
        mTest2ViewModel.getNumLiveData().setValue(5);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTestViewModel.getStatus().setValue("onStop");
        mTest1ViewModel.getNumLiveData().setValue(6);
        mTest2ViewModel.getNumLiveData().setValue(6);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTestViewModel.getStatus().setValue("onDestroy");
        mTest1ViewModel.getNumLiveData().setValue(7);
        mTest2ViewModel.getNumLiveData().setValue(7);
    }
}
