package at.example.zeiterfassung;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import at.example.zeiterfassung.TimeDataAdapter.TimeDataAdapter;
import at.example.zeiterfassung.db.TimeDataContract;
import at.example.zeiterfassung.dialogs.DeleteTimeDataDialog;import at.example.zeiterfassung.dialogs.DeleteTimeDataDialog;
import at.example.zeiterfassung.dialogs.IConfirmDeleteListener;
import at.example.zeiterfassung.dialogs.IDeleteItemListener;


public class ListDataActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, IDeleteItemListener, IConfirmDeleteListener {
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
