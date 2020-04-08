package at.example.zeiterfassung;


import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import at.example.zeiterfassung.TimeDataAdapter.TimeDataAdapter;
import at.example.zeiterfassung.db.TimeDataContract;
import at.example.zeiterfassung.dialogs.DeleteTimeDataDialog;
import at.example.zeiterfassung.dialogs.IConfirmDeleteListener;
import at.example.zeiterfassung.dialogs.IItemActionListener;
import at.example.zeiterfassung.services.CsvExportService;


public class ListDataActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, IItemActionListener, IConfirmDeleteListener {
    private static final int _REQUEST_WRITE_PERMISSION_ID = 100;
    private static final int _LOADER_ID = 100;
    private TimeDataAdapter _adapter = null;
    private RecyclerView _list = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        // Views suchen
        _list = findViewById(R.id.DataList);
        _list.setLayoutManager(new LinearLayoutManager(this));
        _list.setHasFixedSize(true);

        // Adpater initialisieren
        _adapter = new TimeDataAdapter(this, null);
        _list.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(_LOADER_ID, null, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportLoaderManager().destroyLoader(_LOADER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_data, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ExportMenuItem:
                // Abfrage der Berechtigung
                if (ActivityCompat.checkSelfPermission(
                        this, // Context
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == // Benötigte Berechtigung
                        PackageManager.PERMISSION_GRANTED) { // Status der Berechtigung
                    // Berechtigung vorhanden, kann exportiert werden
                    exportCsv();
                } else {
                    // Berechtigung vom Benutzer erfragen
                    ActivityCompat.requestPermissions(
                            this, // Context
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE // Gewünschte Berechtigungen
                            },
                            _REQUEST_WRITE_PERMISSION_ID // ID für Callback
                    );
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Prüfen, von welcher Abfrage die Antwort ankommt
        if (requestCode == _REQUEST_WRITE_PERMISSION_ID) {
            // Prüfen, ob die Berechtigung erteilt wurde
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Berechtigung erteilt, Export kann nun durchgeführt werden
                exportCsv();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void exportCsv() {
        // Service für Export initialisieren
        Intent exportService = new Intent(this, CsvExportService.class);
        // Service starten
        startService(exportService);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        CursorLoader loader = null;

        // Unterscheidung zwischen den Loadern
        switch (loaderId) {
            case _LOADER_ID:
                loader = new CursorLoader(
                        this, // Context
                        TimeDataContract.TimeData.CONTENT_URI, // Daten-URI
                        null, // alle Spalten
                        null, //Filter
                        null, // Filter Argumente
                        TimeDataContract.TimeData.Columns.START_TIME + " DESC" // Sortierung
                );
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Unterscheidung zwischen den Loadern
        switch (loader.getId()) {
            case _LOADER_ID:
                _adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Unterscheidung zwischen den Loadern
        switch (loader.getId()) {
            case _LOADER_ID:
                _adapter.swapCursor(null); // Daten freigeben
                break;
        }
    }

    @Override
    public void deleteItem(long id, int position) {
        // Uri zum löschen erzeugen
        Uri deleteUri = ContentUris.withAppendedId(TimeDataContract.TimeData.CONTENT_URI, id);
        // Löschen
        getContentResolver().delete(deleteUri, null, null);
        // Benachrichtigen, dass ein Datensatz gelöscht wurde (Recycler View)
        _adapter.notifyItemRemoved(position);
    }

    @Override
    public void editItem(long id, int position) {
        Intent editIntent = new Intent(this, EditDataActivity.class);
        // ID des Datensatzen übergeben
        editIntent.putExtra(EditDataActivity.ID_KEY, id);
        startActivity(editIntent);
    }

    @Override
    public void confirmDelete(long id, int position) {
        // Argumente initialisieren
        Bundle arguments = new Bundle();
        arguments.putLong(DeleteTimeDataDialog.ID_KEY, id);
        arguments.putInt(DeleteTimeDataDialog.POSITION_KEY, position);

        // Dialog initialisieren
        DeleteTimeDataDialog dialog = new DeleteTimeDataDialog();
        dialog.setArguments(arguments);

        // Dialog anzeigen
        dialog.show(getSupportFragmentManager(), "DeleteDialog");
    }
}
