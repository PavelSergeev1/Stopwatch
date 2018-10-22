package app.stopwatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Число секунд для вывода на экран
    private int mSeconds = 0;
    private int msecs = 0;
    // Включён ли секундомер
    private boolean mIsRunning = false;
    // Был ли включён секундомер ранее
    private boolean mIsWasRunning;
    // Переменные для отслеживания СТАРТ/СТОП
    public int circleCount = 0;
    boolean circleBoolStop = false;
    // Счётчик нажатий на кнопку круг
    public int circleCountS = 0;
    boolean circleBool = false;
    // Был ли перевёрнут экран
    boolean IsTurndedOver = false;

    // массив с сохранённми результатами кругов
    String[] SavedLaps = new String[1000];
    // переменная для прокрутки списка с кругами в методе run()
    int circleCountRun = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fontPath1 = "fonts/11528.ttf";
        String fontPath2 = "fonts/11678.ttf";
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), fontPath1);
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), fontPath2);

        if (savedInstanceState != null) {
            msecs = savedInstanceState.getInt("msecs");
            mSeconds = savedInstanceState.getInt("seconds");
            mIsRunning = savedInstanceState.getBoolean("isRunning");
            mIsWasRunning = savedInstanceState.getBoolean("isWasRunning");
            circleCountS = savedInstanceState.getInt("circleCountS");
            circleCountRun = savedInstanceState.getInt("circleCountRun");
            SavedLaps = savedInstanceState.getStringArray("SavedLaps");
            IsTurndedOver = savedInstanceState.getBoolean("IsTurndedOver");

            IsTurndedOver = true;

            LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
            LayoutInflater ltinflater = getLayoutInflater();

            int i = 1;
            while (i <= circleCountS) {
                onTurnOverMethod(i, linLayout, ltinflater);
                i++;
            }

            if (IsTurndedOver) {
                for (int j = 0; j < 50; j++){
                    onScroll();
                }
                IsTurndedOver = false;
            }
        }

        TextView textView = (TextView) findViewById(R.id.textViewTime);
        textView.setTypeface(typeface1);

        if (!mIsRunning) {
            Button Start = (Button) findViewById(R.id.buttonStart);
            Start.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
            Start.setTypeface(typeface2);
        }

        if (mIsRunning) {
            Button Start = (Button) findViewById(R.id.buttonStart);
            Start.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorRed));
            Start.setText(R.string.Stop);
            circleBool = false;
        }

        Button Lap = (Button) findViewById(R.id.buttonLap);
        Lap.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorBrown));
        Lap.setTypeface(typeface2);
        Button Reset = (Button) findViewById(R.id.buttonReset);
        Reset.setTypeface(typeface2);
        Button SaveResult = (Button) findViewById(R.id.buttonSave);
        SaveResult.setTypeface(typeface2);
        Button ViewSaved = (Button) findViewById(R.id.buttonViewAllSaved);
        ViewSaved.setTypeface(typeface2);

        runTimer();
    }

    public void onTurnOverMethod(int i, LinearLayout linLayout, LayoutInflater ltinflater){
        if (SavedLaps[i] != null || SavedLaps[i].equals("")) {
            View item = ltinflater.inflate(R.layout.item, linLayout, false);
            TextView tvLapTime = (TextView) item.findViewById(R.id.tvLapTime);

            tvLapTime.setText(String.valueOf(SavedLaps[i]));

            String fontPath1 = "fonts/11528.ttf";
            Typeface typeface1 = Typeface.createFromAsset(getAssets(), fontPath1);

            tvLapTime.setTypeface(typeface1);
            TextView tvLapNumber = (TextView) item.findViewById(R.id.tvLapNumber);
            tvLapNumber.setText(String.valueOf(i));
            tvLapNumber.append(".");
            tvLapNumber.setTypeface(typeface1);

            Log.d("LAP " + String.valueOf(i), SavedLaps[i] + " onTurnOverMethod");

            item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            linLayout.addView(item);
        }
    }

    public void onStartClick(View view){
        // Секундомер запущен
        mIsRunning = true;

        if (circleCount == 0) {
            mIsRunning = true;
            Button Start = (Button) findViewById(R.id.buttonStart);
            Start.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorRed));
            Start.setText(R.string.Stop);
            circleCount++;
            circleBool = false;
        }

        if (circleCount == 1 && circleBool == false) {
            circleBool = true;
            return ;
        }

        if (circleCount == 1) {
            mIsRunning = false;
            Button Start = (Button) findViewById(R.id.buttonStart);
            Start.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorGreen));
            Start.setText(R.string.Start);
            circleBoolStop = true;
            circleCount = 0;
        }

    }

    public void onLapClick(View view){
        if (mIsRunning) {
            circleCountS += 1;

            LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);

            LayoutInflater ltinflater = getLayoutInflater();

            TextView textView = (TextView) findViewById(R.id.textViewTime);

            View item = ltinflater.inflate(R.layout.item, linLayout, false);

            TextView tvLapTime = (TextView) item.findViewById(R.id.tvLapTime);
            tvLapTime.setText(textView.getText().toString());

            TextView tvLapNumber = (TextView) item.findViewById(R.id.tvLapNumber);
            tvLapNumber.setText(String.valueOf(circleCountS));
            tvLapNumber.append(".");

            String fontPath1 = "fonts/11528.ttf";
            Typeface typeface1 = Typeface.createFromAsset(getAssets(), fontPath1);
            tvLapTime.setTypeface(typeface1);
            tvLapNumber.setTypeface(typeface1);

            String laptime = textView.getText().toString();
            SavedLaps[circleCountS] = laptime;

            Log.d("LAP " + String.valueOf(circleCountS), SavedLaps[circleCountS]);

            item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            linLayout.addView(item);
        }
    }

    public void onScroll() {
        ScrollView myScroll = (ScrollView) findViewById(R.id.scroll);
        if (myScroll != null) {
            myScroll.fullScroll(View.FOCUS_DOWN);
        }
    }


    public void onResetClick(View view){
        // Секундомер остановлен и счётчик обнулился
        mIsRunning = false;
        mSeconds = 0;
        circleCount = 0;
        circleCountS = 0;
        circleCountRun = 0;
        circleBool = false;
        circleBoolStop = false;

        TextView timeTextView = (TextView) findViewById(R.id.textViewTime);
        timeTextView.setText("00:00,00");
        TextView Start = (TextView) findViewById(R.id.buttonStart);
        Start.setText(R.string.Start);
        Start.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorGreen));

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
        linLayout.removeAllViews();

        for (int i = 0; i < SavedLaps.length; i++) {
            SavedLaps[i] = null;
        }
    }

    private void runTimer() {
        final TextView timeTextView = (TextView) findViewById(R.id.textViewTime);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int days =  mSeconds / 5184000;
                int hours = mSeconds / 216000;
                int minutes = mSeconds / 3600;
                int secs = (mSeconds % 3600) / 60;
                // int msecs = (int) millis % 1000 / 10;
                msecs = (int) (mSeconds * 1.6667 % 100);
                // Форматируем секунды в часы, минуты и секунды
                String time = String.format("%02d:%02d,%02d", minutes, secs, msecs);
                if (hours > 1 && hours < 24)
                    time = String.format("%02d:%02d:%02d,%02d", hours, minutes, secs, msecs);
                if (days > 1)
                    time = String.format("%02d:%02d:%02d:%02d,%02d", days, hours, minutes, secs, msecs);
                if (timeTextView != null && mIsRunning)
                    timeTextView.setText(time);

                if (mIsRunning) {
                    mSeconds++;
                }

                if (circleCountRun < circleCountS) {
                    circleCountRun = circleCountS;
                    onScroll();
                }

                // Запускаем код снова с задержкой в одну секунду
                handler.postDelayed(this, 1000 % 100);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("seconds", mSeconds);
        outState.putBoolean("isRunning", mIsRunning);
        outState.putBoolean("isWasRunning", mIsWasRunning);
        outState.putInt("msecs", msecs);
        outState.putInt("circleCountS", circleCountS);
        outState.putInt("circleCountRun", circleCountRun);
        outState.putStringArray("SavedLaps", SavedLaps);
        outState.putBoolean("IsTurndedOver", IsTurndedOver);

    }

    public void onSaveButton(View v) {
        if (mIsRunning == true || circleCountS < 1) {
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to save this counting?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(MainActivity.this, SaveActivity.class);
                            intent.putExtra("circleCountS", circleCountS);
                            intent.putExtra("SavedLaps", SavedLaps);
                            intent.putExtra("WhichButtonWasPressed", "onSaveButton");
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void onViewSavedButton(View v) {
        Intent intent = new Intent(MainActivity.this, SaveActivity.class);
        intent.putExtra("WhichButtonWasPressed", "onViewSavedButton");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!mIsRunning) {
            mIsWasRunning = true;
        }

        // mIsWasRunning = mIsRunning;
        // Остановим работу секундомера
        // mIsRunning = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mIsWasRunning) {
            mIsRunning = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsWasRunning)
            mIsRunning = false;

    }
}
