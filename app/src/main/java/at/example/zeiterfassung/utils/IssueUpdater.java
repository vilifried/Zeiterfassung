package at.example.zeiterfassung.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import at.example.zeiterfassung.db.TimeDataContract;
import at.example.zeiterfassung.models.BitbucketIssue;
import at.example.zeiterfassung.models.BitbucketResponse;

public class IssueUpdater extends AsyncTask<Void, Void, Void> {
    // Basis-Zugriffspunkt
    private static final String _ISSUE_BASE_URI =
            "https://api.bitbucket.org/2.0/repositories/webducerbooks/androidbook-changes/issues?q=";

    // Filter
    private static final String _ISSUE_FILTER =
            "(state = \"new\" OR state = \"on hold\" OR state = \"open\")"
                    + " AND updated_on > \"2017-02-01T00:00+02:00\""
                    + " AND component != \"395592\"";
    private static final String _FILTERED_ISSUE_URI =
            _ISSUE_BASE_URI
                    + Uri.encode(_ISSUE_FILTER);

    // Standardrückgabewert
    private final static BitbucketIssue[] _DEFAULT_RETURN_VALUE = new BitbucketIssue[0];
    private final Context _context;

    public IssueUpdater(Context context) {
        _context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Client initialisieren
        try {
            // Parsen der Uri
            URL url = new URL(_FILTERED_ISSUE_URI);

            // Erstellen des Clients
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Defintion der Timeouts
            connection.setReadTimeout(30000); // 30 Sekunden
            connection.setConnectTimeout(60000); // 60 Sekunden

            // Anfrage Header definieren
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Verbinden
            connection.connect();

            // Status der Anfrage prüfen
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                // Fehler bei der Abfrage der Daten
                return null;
            }

            // Laden der Daten
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Lesen der geladenen Daten
            StringBuilder content = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            // Schließen der Resourcen
            reader.close();
            is.close();
            connection.disconnect();

            //parseIssueList(content.toString());
            BitbucketIssue[] issues = parseWithGSon(content.toString());

            // In Datenbak speichern
            saveToDatabase(issues);
        } catch (MalformedURLException e) {
            // URL konnte nicht interpretiert werden
            e.printStackTrace();
        } catch (IOException e) {
            // Fehler beim Zugriff auf das Internet
            e.printStackTrace();
        }

        return null;
    }

    private void saveToDatabase(BitbucketIssue[] issues) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        ContentProviderOperation.Builder builder = ContentProviderOperation.newDelete(TimeDataContract.IssueData.CONTENT_URI);
        operations.add(builder.build());

        for (BitbucketIssue issue : issues) {
            ContentValues values = new ContentValues();
            values.put(TimeDataContract.IssueData.Columns.NUMBER, issue.id);
            values.put(TimeDataContract.IssueData.Columns.TITLE, issue.title);
            values.put(TimeDataContract.IssueData.Columns.PRIORITY, issue.priority);
            values.put(TimeDataContract.IssueData.Columns.STATE, issue.state);

            builder = ContentProviderOperation.newInsert(TimeDataContract.IssueData.CONTENT_URI)
                    .withValues(values);
            operations.add(builder.build());
        }

        try {
            _context.getContentResolver().applyBatch(TimeDataContract.AUTHORITY, operations);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private BitbucketIssue[] parseWithGSon(String content) {
        // Prüfen des Inhaltes
        if (content == null || content.isEmpty()) {
            // Rückgabe einer leeren Liste
            return _DEFAULT_RETURN_VALUE;
        }

        // Initialisieren der Bibliothek
        Gson g = new Gson();
        // Deserialisieren von JSON
        BitbucketResponse response = g.fromJson(content, BitbucketResponse.class);

        return response.values;
    }
}
