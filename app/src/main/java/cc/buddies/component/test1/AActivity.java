package cc.buddies.component.test1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cc.buddies.component.R;


public class AActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("aaaa", "AActivity --> onNewIntent()");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_hello);
        Log.d("aaaa", "AActivity --> onCreate()");

        Button button = findViewById(R.id.button);
        button.setText("A");
        button.setOnClickListener(v -> startActivity(new Intent(this, BActivity.class)));
    }
}
