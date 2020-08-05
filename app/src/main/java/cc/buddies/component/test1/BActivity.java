package cc.buddies.component.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cc.buddies.component.R;


public class BActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("aaaa", "BActivity --> onNewIntent()");
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> startActivity(new Intent(this, AActivity.class)));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_hello);
        Log.d("aaaa", "BActivity --> onCreate()");

        Button button = findViewById(R.id.button);
        button.setText("B");
        button.setOnClickListener(v -> startActivity(new Intent(this, CActivity.class)));
    }
}
