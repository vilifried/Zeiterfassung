package at.example.zeiterfassung.db;

import android.database.sqlite.SQLiteDatabase;

final class TimeDataTable {
    /**
     * ID für eine Auflistung
     */
    public static final int ITEM_LIST_ID = 100;

    /**
     * ID für einen Datensatz
     */
    public static final int ITEM_ID = 101;

    /**
     * ID für einen Datensatz, das noch nicht beendet wurde
     */
    public static final int NOT_FINISHED_ITEM_ID = 102;

    /**
     * Name der Tabelle
     */
    public static final String TABLE_NAME = "time_data";

    /**
     * Skript für die Erzeugung der Tabelle
     */
    private static final String _CREATE_TABLE =
            "CREATE TABLE \"time_data\" ( `_id` INTEGER PRIMARY KEY AUTOINCREMENT, `start_time` TEXT NOT NULL, `end_time` TEXT )";

    static void createTable(SQLiteDatabase db) {
        db.execSQL(_CREATE_TABLE);
    }
}

