package at.example.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import static at.example.zeiterfassung.db.TimeDataContract.IssueData.Columns;

public class IssueDataTable {
    // Konstanten
    /**
     * ID für eine Auflistung
     */
    public static final int ITEM_LIST_ID = 200;

    /**
     * ID für einen Datensatz
     */
    public static final int ITEM_ID = 201;

    /**
     * Name der Tabelle
     */
    public static final String TABLE_NAME = "issue_cache";

    // Script für die Erstellung der Tabelle in der Datenbank
    private static final String _CREATE =
            "CREATE TABLE `" + TABLE_NAME + "` ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TimeDataContract.IssueData.Columns.NUMBER + " INTEGER NOT NULL UNIQUE,"
                    + TimeDataContract.IssueData.Columns.TITLE + " TEXT,"
                    + TimeDataContract.IssueData.Columns.PRIORITY + " TEXT,"
                    + TimeDataContract.IssueData.Columns.STATE + " TEXT"
                    + ")";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(_CREATE);
    }

    public static void updateTable(SQLiteDatabase db, int oldVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            case 3:
                // Migration von Version 3 auf 4
                db.execSQL(_CREATE);
        }
    }
}
