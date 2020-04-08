package at.example.zeiterfassung.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import at.example.zeiterfassung.db.TimeDataContract;


public class CsvExporter extends AsyncTask<Void, Integer, Void> {
    private final Context _context;

    public CsvExporter(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Cursor data = null;

        try {
            // Daten über Content Provider abfragen
            data = _context.getContentResolver()
                    .query(TimeDataContract.TimeData.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            int dataCount = data == null ? 0 : data.getCount();

            if (dataCount == 0) {
                // Nichts weiter machen, wenn keine Daten vorhanden
                return null;
            }

            // Ordner für externe Daten
            File externalStorage = Environment.getExternalStorageDirectory();

            // Prüfen, ob externe Daten geschrieben werden können (SD Karte nur Read Only oder voll)
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return null;
            }

            // Unterordner für unser Export
            File exportPath = new File(externalStorage, "export");

            // Dateiname für Export
            File exportFile = new File(exportPath, "TimeDataLog.csv");

            // Erzeugen der ordner, falls noch nicht vorhanden
            if (!exportFile.exists()) {
                exportPath.mkdirs();
            }

            // Klasse zum Schreiben der Daten
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(exportFile));

                // Asulesen der Spaltennamen
                String[] columnList = data.getColumnNames();

                StringBuilder line = new StringBuilder();

                // Befüllen der ersten Zeile mit Spaltennamen
                for (String columnName : columnList) {
                    if (line.length() > 0) {
                        line.append(';');
                    }

                    line.append(columnName);
                }

                writer.append(line);

                // Zeilen mit Daten ausgeben
                while (data.moveToNext()) {
                    // Neue Zeile
                    writer.newLine();

                    // Zeilenvariable leeren
                    line.delete(0, line.length());

                    // Ausgabe aller Spaltenwerte
                    for (int columnIndex = 0; columnIndex < columnList.length; columnIndex++) {
                        if (line.length() > 0) {
                            line.append(';');
                        }

                        // Prüfen auf NULL (Datenbank) des Spalteninhaltes
                        if (data.isNull(columnIndex)) {
                            line.append("<NULL>");
                        } else {
                            line.append(data.getString(columnIndex));
                        }
                    }

                    writer.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.flush();

                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        } finally {
            if (data != null) {
                data.close();
            }
        }
    }
}

