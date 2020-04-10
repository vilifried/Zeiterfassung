package at.example.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static final String _DB_FILE_NAME = "time_tracking.db";
    private static final int _DB_VERSION = 4;

    public DbHelper(@Nullable Context context) {
        super(context, _DB_FILE_NAME, null, _DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TimeDataTable.createTable(db);
        IssueDataTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TimeDataTable.updateTable(db, oldVersion);
        IssueDataTable.updateTable(db, oldVersion);
    }
}



