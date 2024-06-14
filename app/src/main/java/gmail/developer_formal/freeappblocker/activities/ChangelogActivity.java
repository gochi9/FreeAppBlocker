package gmail.developer_formal.freeappblocker.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.adapters.ChangelogAdapter;
import gmail.developer_formal.freeappblocker.objects.Changelog;
import gmail.developer_formal.freeappblocker.objects.ChangelogEntry;

public class ChangelogActivity extends AppCompatActivity {

    private static final String CHANGELOG_URL = "https://raw.githubusercontent.com/gochi9/FreeAppBlocker/master/app/src/main/java/changelog.json?token=$(date%20+%s";

    private TextView versionTextView;
    private RecyclerView changelogRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setContentView(R.layout.activity_changelog);

        versionTextView = findViewById(R.id.versionTextView);
        changelogRecyclerView = findViewById(R.id.changelogRecyclerView);

        changelogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        displayAppVersion();
        fetchChangelog();
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.finish();
        Intent dialogIntent = new Intent(this, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }

    private void displayAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            versionTextView.setText("Your Version: " + versionName);
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("ChangelogActivity", "Failed to get version name", e);
        }
    }

    private void fetchChangelog() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CHANGELOG_URL,
                this::parseAndDisplayChangelog, error -> {
            Toast.makeText(ChangelogActivity.this, "Failed to fetch changelog", Toast.LENGTH_SHORT).show();
            Log.e("ChangelogActivity", "Error fetching changelog", error);
        });

        queue.add(stringRequest);
    }

    private void parseAndDisplayChangelog(String jsonResponse) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<Changelog>() {}.getType();
            Changelog changelog = gson.fromJson(jsonResponse, type);

            List<ChangelogEntry> changelogEntries = changelog.getChangelog();
            ChangelogAdapter changelogAdapter = new ChangelogAdapter(this, changelogEntries);
            changelogRecyclerView.setAdapter(changelogAdapter);
        }
        catch (JsonSyntaxException e) {
            Log.e("ChangelogActivity", "Failed to parse changelog", e);
            Toast.makeText(this, "Failed to parse changelog", Toast.LENGTH_SHORT).show();
        }
    }
}
