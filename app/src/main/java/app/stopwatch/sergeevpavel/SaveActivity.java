package app.stopwatch.sergeevpavel;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SaveActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

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
    String[] SavedLapsSDB = new String[1010];
    int circleCountSDB;

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

            onViewNumberOfSavedCountings();
        } else if (whichButtonWasPressed.equals("onViewSavedButton")) {
            onOpenActivityViewSaved();
        }

        mDBHelper = new DBHelper(this);
    }

    public void onViewNumberOfSavedCountings() {
        setContentView(R.layout.activity_save);

        int idcounting = 0;
        String number = "";

        String fontPath2 = "fonts/11678.ttf";
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), fontPath2);

        TextView tvNumberOfSavedCountings = (TextView) findViewById(R.id.tvNumberOfSavedCountings);
        TextView textViewSaveDB = (TextView) findViewById(R.id.textViewSaveDB);
        TextView tvNumberOsSavedText = (TextView) findViewById(R.id.tvNumberOsSavedText);

        mDBHelper = new DBHelper(this);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        Cursor c = db.query(FIRST_TABLE, null, null, null, null, null, null);

        c.moveToFirst();
        if (c.moveToFirst()) {
            do {
                idcounting = c.getColumnIndex("idcounting");
                number = String.valueOf(c.getInt(idcounting));
            } while (c.moveToNext());
        } else number = "0";

        tvNumberOfSavedCountings.setText(number);
        tvNumberOfSavedCountings.setTypeface(typeface2);
        textViewSaveDB.setTypeface(typeface2);
        tvNumberOsSavedText.setTypeface(typeface2);

        c.close();
    }

    public void onOpenActivityViewSaved() {
        setContentView(R.layout.activity_view_saved);

        String fontPath2 = "fonts/11678.ttf";
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), fontPath2);

        TextView tvSC = (TextView) (findViewById(R.id.tvSC));
        TextView tvIC = (TextView) (findViewById(R.id.tvIC));
        TextView tvCN = (TextView) (findViewById(R.id.tvCN));
        TextView tvLIC = (TextView) (findViewById(R.id.tvLIC));
        tvSC.setTypeface(typeface2);
        tvIC.setTypeface(typeface2);
        tvCN.setTypeface(typeface2);
        tvLIC.setTypeface(typeface2);

        Button btnDeleteAllCountings = (Button) findViewById(R.id.btnDeleteDatabase);
        btnDeleteAllCountings.setTypeface(typeface2);

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linearLayoutVS);
        LayoutInflater ltinflater = getLayoutInflater();

        int tag = 1;

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
                String first = String.valueOf(c.getInt(idcounting)) + ".";
                IdCounting.setText(first);
                IdCounting.setTypeface(typeface2);

                TextView LapsInCounting = (TextView) item.findViewById(R.id.tvLapsInCounting);
                String second = String.valueOf(c.getInt(lapsincounting));
                LapsInCounting.setText(String.valueOf(second));
                LapsInCounting.setTypeface(typeface2);

                TextView CountingName = (TextView) item.findViewById(R.id.tvCountingName);
                String third = c.getString(countingname);
                CountingName.setText(String.valueOf(third));
                CountingName.setTypeface(typeface2);

                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item);

                item.setClickable(true);
                item.setOnClickListener(this);
                item.setOnLongClickListener(this);
                item.setTag(tag);
                tag++;
            } while (c.moveToNext());
        }
        c.close();
    }

    @Override
    public void onClick(View v) {
        // количество записанных отсчётов
        int lastcounting = 0;
        int[] numberoflaps = new  int[1000];

        DBHelper mDBHelper = new DBHelper(this);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c1 = db.query(FIRST_TABLE, null, null, null,
                null, null, null);
        if (c1.moveToFirst()) {
            int idcounting = c1.getColumnIndex("idcounting");

            do {
                lastcounting = c1.getInt(idcounting);
            } while (c1.moveToNext());

            numberoflaps = new int[lastcounting];
            int i = 0;

            c1.moveToFirst();
            int lapsincounting = c1.getColumnIndex("lapsincounting");
            do {
                numberoflaps[i] = c1.getInt(lapsincounting);
                i++;
            } while (c1.moveToNext());

        }
        c1.close();

        int tag2 = (int) v.getTag();
        if (lastcounting != 0) {
            for (int j = 1; j <= lastcounting; j++) {
                if (tag2 == j){
                    onViewPressedCounting(db, lastcounting, tag2, numberoflaps);
                }
            }
        }
    }

    @Override
    public boolean onLongClick(final View v) {
        final int tag = (int) v.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String DeleteCounting = getString(R.string.DeleteCounting);
        String Yes = getString(R.string.Yes);
        String Cancel = getString(R.string.Cancel);
        builder.setMessage(DeleteCounting + String.valueOf(tag) +  " " +
                "?")
                .setCancelable(true)
                .setPositiveButton(Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDeleteCounting(tag);
                    }
                })
                .setNegativeButton(Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        onOpenActivityViewSaved();
        return false;
    }

    private void onDeleteCounting(int tag) {
        DBHelper mDBHelper = new DBHelper(this);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c1 = db.query(FIRST_TABLE, null, null, null,
                null, null, null);
        Cursor c2 = db.query(SECOND_TABLE, null, null, null,
                null, null, null);

        int lapsincounting = 0;
        int firstLap = 0;
        int lastLap = 0;
        int[] numberoflaps = new  int[1010];
        int i = 0;

        c1.moveToFirst();
        lapsincounting = c1.getColumnIndex("lapsincounting");
        do {
            numberoflaps[i] = c1.getInt(lapsincounting);
            i++;
        } while (c1.moveToNext());

        if (c1.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idcounting = c1.getColumnIndex("idcounting");

            do {
                if (tag == c1.getInt(idcounting)) {
                    int counting = c1.getColumnIndex("countingname");
                    String name = c1.getString(counting);
                    String countingname = c1.getString(idcounting);
                    db.delete(FIRST_TABLE, "idcounting = " + countingname, null);
                    for (int j = 1; j < tag; j++){
                        firstLap += numberoflaps[j];
                    }
                    lastLap = firstLap + lapsincounting;
                    c2.move(firstLap);
                    for (int w = firstLap; w <= lastLap; w++) {
                        db.delete(SECOND_TABLE, "id = " + w, null);
                    }
                    String text1 = getString(R.string.Counting);
                    String text2 = getString(R.string.deleted);
                    Toast.makeText(this, text1 + name +
                            text2 , Toast.LENGTH_LONG).show();
                }
            } while (c1.moveToNext());
        }

        ContentValues cv1 = new ContentValues();
        int y = 1;

        Cursor c11 = db.query(FIRST_TABLE, null, null, null,
                null, null, null);

        if (c11.moveToFirst()) {
            do {
                int idcounting = c11.getColumnIndex("idcounting");
                String countingname = c11.getString(idcounting);
                cv1.put("idcounting", y);
                db.update(FIRST_TABLE, cv1, "idcounting = " + countingname, null);
                y++;
            } while (c11.moveToNext());
        }

        ContentValues cv2 = new ContentValues();
        y = 1;

        Cursor c22 = db.query(SECOND_TABLE, null, null, null,
                null, null, null);

        if (c22.moveToFirst()) {
            do {
                int id = c22.getColumnIndex("id");
                String countingname = c22.getString(id);
                cv2.put("id", y);
                db.update(SECOND_TABLE, cv2, "id = " + countingname, null);
                y++;
            } while (c22.moveToNext());
        }

        c1.close();
        c2.close();
        onOpenActivityViewSaved();
    }

    private void onViewPressedCounting(SQLiteDatabase db, int lastcounting, int i, int[] numberoflaps) {
        setContentView(R.layout.activity_saved_counting);

        String fontPath1 = "fonts/11528.ttf";
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), fontPath1);
        String fontPath2 = "fonts/11678.ttf";
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), fontPath2);

        Button Back = (Button) findViewById(R.id.btnBack);
        Back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOpenActivityViewSaved();
                }
        });

        TextView tvPressedCountingName = (TextView) (findViewById(R.id.tvPressedCountingName));
        TextView tvL = (TextView) (findViewById(R.id.tvL));
        TextView tvLT = (TextView) (findViewById(R.id.tvLT));
        tvPressedCountingName.setTypeface(typeface2);
        tvL.setTypeface(typeface2);
        tvLT.setTypeface(typeface2);

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linearLayoutSC);
        LayoutInflater ltinflater = getLayoutInflater();

        int firstlap = 1;
        int lastlap = 0;

        for (int j = 0; j < i - 1; j++){
            firstlap += numberoflaps[j];
        }

        for (int j = 0; j < i; j++) {
            if (j == i - 1) {
                lastlap = firstlap + numberoflaps[j];
            }
        }

        Cursor c1 = db.query(FIRST_TABLE, null, null,
                null, null, null, null);

        c1.move(i);

        int countingname = (c1.getColumnIndex("countingname"));

        TextView CountingName = (TextView) findViewById(R.id.tvPressedCountingName);
        String name = c1.getString(countingname);
        CountingName.setText(String.valueOf(name));

        Cursor c2 = db.query(SECOND_TABLE, null,null,null,
                null,null, null);

        if (c2.move(firstlap)) {
            int LapColIndex = c2.getColumnIndex("lap");

            int lapnumber = 1;

            do {
                View item = ltinflater.inflate(R.layout.item, linLayout, false);

                TextView tvLapTime = (TextView) item.findViewById(R.id.tvLapTime);
                String Lap = c2.getString(LapColIndex);
                tvLapTime.setText(Lap);
                tvLapTime.setTypeface(typeface1);

                TextView tvLapNumber = (TextView) item.findViewById(R.id.tvLapNumber);
                String LapNumber = String.valueOf(lapnumber) + ".";
                tvLapNumber.setText(String.valueOf(LapNumber));
                tvLapNumber.setTypeface(typeface1);

                item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                linLayout.addView(item);

                lastlap--;
                lapnumber++;

                c2.moveToNext();
            } while (lastlap > firstlap);
        }
        c2.close();
        c1.close();
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

            Intent intent = new Intent(SaveActivity.this, MainActivity.class);
            startActivity(intent);
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

            int ID;

            do {
                ID = c1.getInt(idcounting);
            } while (c1.moveToNext());

            // записываем измерение
            cv1.put("idcounting", ID + 1);
            cv1.put("lapsincounting", circleCountSDB);
            cv1.put("countingname", editTextNameDB.getText().toString());
            db.insert(FIRST_TABLE, null, cv1);
        } else {
            cv1.put("idcounting", 1);
            cv1.put("lapsincounting", circleCountSDB);
            cv1.put("countingname", editTextNameDB.getText().toString());
            db.insert(FIRST_TABLE, null, cv1);

            c1 = db.query(FIRST_TABLE, null,null,null,
                    null,null, null);
        }
        c1.close();

        String text1 = getString(R.string.Counting);
        String text2 = getString(R.string.SuccessfullySaved);
        Toast.makeText(this, text1 + editTextNameDB.getText().toString() +
                text2 , Toast.LENGTH_LONG).show();
    }

    private void WriteToTheSecondTable(SQLiteDatabase db) {
        // создаём объект для данных
        ContentValues cv2 = new ContentValues();

        String LapTime;

        for (int i = 1; i <= circleCountSDB; i++) {
            LapTime = SavedLapsSDB[i];
            cv2.put("lap", LapTime);
            // вставляем запись и получаем её ID
            db.insert(SECOND_TABLE, null, cv2);
        }
    }

    public void onClickDeleteDatabase(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String DoYouWantToDeleteAllCountings = getString(R.string.DoYouWantToDeleteAllCountings);
        String Yes = getString(R.string.Yes);
        String Cancel = getString(R.string.Cancel);
        builder.setMessage(DoYouWantToDeleteAllCountings)
                .setCancelable(true)
                .setPositiveButton(Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // подключаемся к БД
                        SQLiteDatabase db = mDBHelper.getWritableDatabase();

                        // удаляем таблицы
                        db.execSQL("DROP TABLE IF EXISTS " + FIRST_TABLE);
                        db.execSQL("DROP TABLE IF EXISTS " + SECOND_TABLE);

                        // создаём таблицы заново
                        db.execSQL(CREATE_FIRST_TABLE);
                        db.execSQL(CREATE_SECOND_TABLE);

                        // удалть все item
                        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linearLayoutVS);
                        if (linLayout != null) linLayout.removeAllViews();

                        Intent intent = new Intent(SaveActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        onOpenActivityViewSaved();
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, myDatabase, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FIRST_TABLE);
            db.execSQL(CREATE_SECOND_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
