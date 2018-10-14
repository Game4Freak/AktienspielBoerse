package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "aktienspieldaten";
    public static final String TABLE = "aktienspiel";
    public static final String KEY_ID = "id";
    public static final String KEY_WERT = "wert";
    public static final String KEY_NAME = "companyname";
    public static final String KEY_DESCRIPTION = "description";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WERT + " INTEGER,"
                + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        onCreate(db);

    }
}
