package com.example.copytext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final ArrayList<Text> texts = new ArrayList<>();
    TextAdapter adapter;
    ListView listView;
    int id = 0; // for saving unique Texts to Shared Preferences
    SharedPreferences savedTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TextAdapter(this, texts);
        savedTexts = getSharedPreferences("texts", MODE_PRIVATE);
        listView = findViewById(R.id.list);

        registerForContextMenu(listView);

        readFromGson();
        updateAdapter();
    }

    private void updateAdapter() {
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Text t = texts.get(position);
                CustomDialog c = new CustomDialog(MainActivity.this);
                c.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(String type, String content) {
                        t.setType(type);
                        t.setContent(content);
                        /* Save to Shared Preferences */
                        SharedPreferences.Editor prefsEditor = savedTexts.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(t);
                        prefsEditor.putString(""+t.getId(), json);
                        prefsEditor.apply();
                    }
                });
                c.show();
            }
        });
    }

    private void readFromGson() {
        Gson gson = new Gson();
        Map<String,?> keys = savedTexts.getAll();

        /* Loop through existing Notifications to load when app starts */
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String json = entry.toString().substring(2);
            Text t = gson.fromJson(json, Text.class);
            texts.add(t);
            id = t.getId(); // keep track of last item's id
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        texts.add(new Text("Hold To Copy/Delete", "", ++id));
        updateAdapter();
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Copy");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Text t = texts.get(info.position);

        /* Remove from Shared Preferences */
        if (item.getTitle() == "Delete") {
            SharedPreferences.Editor prefsEditor = savedTexts.edit();
            prefsEditor.remove("" + t.getId());
            prefsEditor.apply();

            /* Remove from ArrayList (to remove from ListView) */
            texts.remove(info.position);
            updateAdapter();
        }

        /* Put on clipboard */
        else if (item.getTitle() == "Copy") {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", t.getContent());
            clipboard.setPrimaryClip(clip);
        }

        return true;
    }
}
