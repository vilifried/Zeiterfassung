package at.example.zeiterfassung;

import android.database.Cursor;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import at.example.zeiterfassung.adapter.IssueAdapter;
import at.example.zeiterfassung.db.TimeDataContract;
import at.example.zeiterfassung.utils.IssueUpdater;

import at.example.zeiterfassung.utils.IssueUpdater;

public class InfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int _LOADER_ID = 200;
    private RecyclerView _issueList;
    private IssueAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Initialisierung der UI
        _issueList = findViewById(R.id.IssueList);
        _issueList.setLayoutManager(new LinearLayoutManager(this));
        _adapter = new IssueAdapter(this, null);
        _issueList.setHasFixedSize(true);
        _issueList.setAdapter(_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Daten mit Updater lesen
        new IssueUpdater(getApplicationContext()).execute();
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

        switch (loaderId) {
            case _LOADER_ID:
                loader = new CursorLoader(this,
                        TimeDataContract.IssueData.CONTENT_URI,
                        null,
                        null,
                        null,
                        TimeDataContract.IssueData.Columns.NUMBER + " DESC");
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case _LOADER_ID:
                _adapter.swapData(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case _LOADER_ID:
                _adapter.swapData(null);
                break;
        }
    }
}
