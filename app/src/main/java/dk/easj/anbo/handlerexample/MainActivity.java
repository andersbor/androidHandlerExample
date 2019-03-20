package dk.easj.anbo.handlerexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

// https://stackoverflow.com/questions/4597690/android-timer-how-to
public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private long startTime = 0;

    //runs without a timer by re-posting this handler at the end of the runnable
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {

        // will NOT run in the background. AsyncTask needed for network access.
        @Override
        public void run() {
            doIt();
            timerHandler.postDelayed(this, 10000);
        }
    };

    private void doIt() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        Locale locale = Locale.getDefault();
        timerTextView.setText(String.format(locale, "%d:%02d", minutes, seconds));

        // NetworkOnMainThreadException
        //CharSequence response = HttpHelper.GetHttpResponse("https://berthawristbandrestprovider.azurewebsites.net/api/wristbanddata");
        //Log.d("MINE", response.toString());

        MyReader task = new MyReader();
        task.execute("https://berthawristbandrestprovider.azurewebsites.net/api/wristbanddata");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);

        Button b = findViewById(R.id.button);
        b.setText("start");
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");
                } else {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("stop");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button b = findViewById(R.id.button);
        b.setText("start");
    }

    private class MyReader extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence charSequence) {
            super.onPostExecute(charSequence);
            TextView view = findViewById(R.id.mainHttpTextView);
            view.setText(charSequence.toString());
        }
    }
}
