package at.example.zeiterfassung;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    private WebView _webContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Initialisierung der UI
        _webContent = findViewById(R.id.WebContent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Google anzeigen
        _webContent.loadUrl("https://www.google.de");
    }
}
