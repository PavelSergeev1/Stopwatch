package app.stopwatch;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SaveActivity extends AppCompatActivity {

    // имя базы данных
    private static final String myDatabase = "myDB";
    // имена таблиц в базе данных
    private static final String FIRST_TABLE = "firsttable";
    private static final String SECOND_TABLE = "secondtable";
    // заявления о создании таблиц
    private static final String CREATE_FIRST_TABLE = "CREATE TABLE " + FIRST_TABLE +
            "(" + " idcounting integer primary key autoincrement," + "lapsincounting integer,"
            + "countingname text" + ")";
    private static final String CREATE_SECOND_TABLE = "CREATE TABLE " + SECOND_TABLE +
            "(" + " id integer primary key autoincrement," + "lap text" + ")";


    // массив с сохранённми результатами кругов
    String[] SavedLapsSDB = new String[1000];
    int circleCountSDB;

    String LOG_TAG = "myLogs";

    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        Intent intent = getIntent();

        String whichButtonWasPressed = intent.getStringExtra("WhichButtonWasPressed");
        if (whichButtonWasPressed.equals("onSaveButton")) {
            int count = intent.getIntExtra("circleCountS", 0);

            String array[] = intent.getStringArrayExtra("SavedLaps");

            circleCountSDB = count;
            for (int i = 1; i <= circleCountSDB; i++) {
                SavedLapsSDB[i] = array[i];
            }
        } else if (whichButtonWasPressed.equals("onViewSavedButton")) {
            onOpenActivityViewSaved();
        }


        mDBHelper = new DBHelper(this);
    }

    public void onOpenActivityViewSaved() {
        setContentView(R.layout.activity_view_saved);

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linearLayoutVS);
        LayoutInflater ltinflater = getLayoutInflater();

        mDBHelper = new DBHelper(this);

        // подключаемся к БД
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        Cursor c = db.query(FIRST_TABLE, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idcounting = c.getColumnIndex("idcounting");
            int lapsincounting = c.getColumnIndex("lapsincounting");
            int countingname = (c.getColumnIndex("countingname"));

            do {
                View item = ltinflater.inflate(R.layout.item_view_saved, linLayout, false);

                TextView IdCounting = (TextView) item.findViewById(R.id.tvIdCounting);
                String first = String.valueOf(c.getInt(idcounting));
                IdCounting.setText(first);

                TextView LapsInCounting = (TextView) item.findViewById(R.id.tvLapsInCounting);
                String second = String.valueOf(c.getInt(lapsincounting));
                LapsInCounting.setText(String.valueOf(second));

                TextView CountingName = (TextView) item.findViewById(R.id.tvCountingName);
                String third = c.getString(countingname);
                CountingName.setText(String.valueOf(third));

                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item);
            } while (c.moveToNext());
        }
        c.close();
    }

    public void onClickSaveDB(View view) {
        EditText editTextNameDB = (EditText) findViewById(R.id.etSavedDB);
        if (!editTextNameDB.getText().toString().equals("")) {
            // подключаемся к БД
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            // делаем запись в первой таблице
            WriteToTheFirstTable(db, editTextNameDB);
            // очищаем поле editTextNameDB
            editTextNameDB.getText().clear();
            // делаем запись во второй таблице
            WriteToTheSecondTable(db);

        }
    }

    private void WriteToTheFirstTable(SQLiteDatabase db, EditText editTextNameDB) {
        // создаём объект для данных
        ContentValues cv1 = new ContentValues();
        Cursor c1 = db.query(FIRST_TABLE, null,null,null,
                null,null, null);
        // проверяем первая ли это запись отсчёта
        if(c1.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idcounting = c1.getColumnIndex("idcounting");
            int lapsincounting = c1.getColumnIndex("lapsincounting");
            int countingname = (c1.getColumnIndex("countingname"));

            int ID;

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG, "COUNTING ID = " + c1.getInt(idcounting)
                        + ", LAPS IN COUNTING = " + c1.getInt(lapsincounting)
                        + ", COUNTING NAME = " + c1.getString(countingname));
                ID = c1.getInt(idcounting);
                // переход на следующую строку
                // а если следующей нет, то false - выходим из цикла
            } while (c1.moveToNext());

            // записываем измерение
            cv1.put("idcounting", ID + 1);
            cv1.put("lapsincounting", circleCountSDB);
            cv1.put("countingname", editTextNameDB.getText().toString());
            db.insert(FIRST_TABLE, null, cv1);

        } else {
            Log.d(LOG_TAG, "0 COUNTINGS SAVED");

            cv1.put("idcounting", 1);
            cv1.put("lapsincounting", circleCountSDB);
            cv1.put("countingname", editTextNameDB.getText().toString());
            db.insert(FIRST_TABLE, null, cv1);

            c1 = db.query(FIRST_TABLE, null,null,null,
                    null,null, null);

            int idcounting = c1.getColumnIndex("idcounting");
            int lapsincounting = c1.getColumnIndex("lapsincounting");
            int countingname = (c1.getColumnIndex("countingname"));

            c1.moveToFirst();
            Log.d(LOG_TAG, "COUNTING ID = " + c1.getInt(idcounting)
                    + ", LAPS IN COUNTING = " + c1.getInt(lapsincounting)
                    + ", COUNTING NAME = " + c1.getString(countingname));
        }
        c1.close();
    }

    private void WriteToTheSecondTable(SQLiteDatabase db) {
        // создаём объект для данных
        ContentValues cv2 = new ContentValues();

        String LapTime;

        Log.d(LOG_TAG, "--- Insert in secondtable ---");

        for (int i = 1; i <= circleCountSDB; i++) {
            LapTime = SavedLapsSDB[i];
            cv2.put("lap", LapTime);
            // вставляем запись и получаем её ID
            long rowID = db.insert(SECOND_TABLE, null, cv2);
            Log.d(LOG_TAG, "row inserted, ID = " + rowID);
        }

        // делаем запрос всех данных из таблицы secondtable, получаем Cursor
        Cursor c = db.query(SECOND_TABLE, null,null,null,null,null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернётся false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int LapColIndex = c.getColumnIndex("lap");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex)
                        + ", lap = " + c.getString(LapColIndex) );
                // переход на следующую строку
                // а если следующей нет, то false - выходим из цикла
            } while (c.moveToNext());
        }
        c.close();
    }

    public void onClickViewDatabase(View view){
        if (circleCountSDB >= 1){
            LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout333);
            LayoutInflater ltinflater = getLayoutInflater();

            onViewLap(linLayout, ltinflater);

        } else Log.d(LOG_TAG, "circleCountSDB = " + String.valueOf(circleCountSDB));
    }

    public void onViewLap(LinearLayout linLayout, LayoutInflater ltinflater){

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = db.query("secondtable", null,null,null,null,null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int LapColIndex = c.getColumnIndex("lap");

            do {
                View item = ltinflater.inflate(R.layout.item2, linLayout, false);

                TextView tvLapTime = (TextView) item.findViewById(R.id.tvLapTime);
                String Lap = c.getString(LapColIndex);
                tvLapTime.setText(Lap);

                TextView tvLapNumber = (TextView) item.findViewById(R.id.tvLapNumber);
                String LapNumber = String.valueOf(c.getInt(idColIndex)) + ".";
                tvLapNumber.setText(String.valueOf(LapNumber));

                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item);
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        c.close();
    }

    public static void onViewSavedCountings(){

    }

    public void onClickDeleteDatabase(View view) {
        // подключаемся к БД
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // удаляем таблицы
        db.execSQL("DROP TABLE IF EXISTS " + FIRST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SECOND_TABLE);

        // создаём таблицы заново
        db.execSQL(CREATE_FIRST_TABLE);
        db.execSQL(CREATE_SECOND_TABLE);
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, myDatabase, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- OnCreate tables ---");
            // создаём таблицу с полями
            db.execSQL(CREATE_FIRST_TABLE);
            db.execSQL(CREATE_SECOND_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
