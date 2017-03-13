package ravishankar.com.tappydefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
    TextView FastestTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FastestTime=(TextView)findViewById(R.id.textHighScore);
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs=getSharedPreferences("HiScores",MODE_PRIVATE);
        final Button buttonplay=(Button)findViewById(R.id.buttonPlay);
        buttonplay.setOnClickListener(this);
        long fastestTime=prefs.getLong("fastestTime",1000000);
        FastestTime.setText("Fastest Time:" + fastestTime);
    }

    @Override
    public void onClick(View v) {
        Intent i=new Intent(this,GameActivity.class);
        startActivity(i);
        finish();
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return false;
    }
}
