package de.gym_kirchseeon.aktienspielboerse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {
    private AlphaVantageDownloader restClient;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "aktienspielData";
    public static final String TABLE = "companyShares";
    public static final String KEY_ID = "id";
    // Abkürzung des Unternehmens
    public static final String KEY_SHORT = "companyShort";
    // Name des Unternehmens
    public static final String KEY_NAME = "companyName";
    // letzten 100 werte
    public static final String[] KEY_SHARES = new String[100];
    // anzahl der gekauften Aktien
    public static final String KEY_AMOUNT = "sharesAmount";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        for (int i = 0; i < 100; i++){
            KEY_SHARES[i] = "share" + i;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHORT + " TEXT,"
                + KEY_NAME + " TEXT,";
        for (int i = 0; i < KEY_SHARES.length; i++){
            CREATE_TABLE = CREATE_TABLE + KEY_SHARES[i] + " FLOAT,";
        }
        CREATE_TABLE = CREATE_TABLE + KEY_AMOUNT + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        onCreate(db);
    }

    public void updateDatabaseAll(){
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> shortList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
               shortList.add(cursor.getString(1));
            } while(cursor.moveToNext());
        }

        db = this.getWritableDatabase();

        for ( String n: shortList ){
            JSONObject shares = new JSONObject(); //Restclient getCompany... n

            ContentValues values = new ContentValues();
            for (int i = 0; i < KEY_SHARES.length; i++){
                values.put(KEY_SHARES[i], "22.2"); //jeder einzelne eintrag
            }
            db.update(TABLE, values, KEY_SHORT + "=?", new String[]{n});
        }
    }

    public void updateDatabaseByShort(String companyShort){
        SQLiteDatabase db = this.getWritableDatabase();
        JSONObject shares = new JSONObject(); //Restclient getCompany... companyShort

        ContentValues values = new ContentValues();
        for (int i = 0; i < KEY_SHARES.length; i++){
            values.put(KEY_SHARES[i], "22.2"); //jeder einzelne eintrag
        }
        db.update(TABLE, values, KEY_SHORT + "=?", new String[]{companyShort});
    }

    public void updateSharesAmountByCompanyShort(String companyShort, int amount){

    }

    public JSONObject getCompanyByCompanyShort(String companyShort){
        return new JSONObject();
    }

    public List<JSONObject> getAllCompanies() {
        return new ArrayList<JSONObject>();
    }
}
