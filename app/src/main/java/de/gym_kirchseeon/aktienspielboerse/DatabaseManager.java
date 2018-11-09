package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "aktienspielData";
    public static final String TABLE = "companyShares";
    public static final String KEY_ID = "id";
    // Abkürzung des Unternehmens
    public static final String KEY_SHORT = "companyShort";
    // Name des Unternehmens
    public static final String KEY_NAME = "companyName";
    // letzter Wert vor den aktuellen Wert, um Aussage über Kursänderung treffen zu können (Kurs steigt / sinkt)
    public static final String KEY_SHARES0 = "companyShare0";
    // aktueller Wert
    public static final String KEY_SHARES1 = "companyShare1";
    // anzahl der gekauften Aktien
    public static final String KEY_AMOUNT = "sharesAmount";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHORT + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_SHARES0 + " FLOAT," + KEY_SHARES1 + " FLOAT," + KEY_AMOUNT + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        onCreate(db);

    }
}
