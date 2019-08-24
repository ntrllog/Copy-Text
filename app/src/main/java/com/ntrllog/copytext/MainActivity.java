package com.ntrllog.copytext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final ArrayList<Text> textArrayList = new ArrayList<>();
    TextAdapter adapter;
    ListView listView;
    int id = 0; // for saving unique Texts to Shared Preferences
    SharedPreferences textSharedPreferences;
    SharedPreferences idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TextAdapter(this, textArrayList);
        textSharedPreferences = getSharedPreferences("texts", MODE_PRIVATE);
        idList = getSharedPreferences("id", MODE_PRIVATE);
        listView = findViewById(R.id.list);

        registerForContextMenu(listView);
        updateAdapter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idList.getInt("text_id_key", 0);
                textArrayList.add(new Text("Tap to copy/Hold to edit or delete", "", id));
                idList.edit().putInt("text_id_key", (id+1) % Integer.MAX_VALUE).apply();
                listView.setAdapter(adapter);
            }
        });
    }

    private void updateAdapter() {
        listView.setAdapter(adapter);

        Gson gson = new Gson();
        Map<String,?> keys = textSharedPreferences.getAll();

        /* Loop through existing Notifications to load when app starts */
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String json = entry.getValue().toString();
            Text t = gson.fromJson(json, Text.class);
            textArrayList.add(t);
            id = t.getId(); // keep track of last item's id
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Text t = textArrayList.get(position);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", t.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Text t = textArrayList.get(info.position);

        if (item.getTitle() == "Delete") {
            SharedPreferences.Editor prefsEditor = textSharedPreferences.edit();
            prefsEditor.remove("" + t.getId());
            prefsEditor.apply();

            /* Remove from ArrayList (to remove from ListView) */
            textArrayList.remove(info.position);
            listView.setAdapter(adapter);
        }
        else if (item.getTitle() == "Edit") {
            CustomDialog c = new CustomDialog(MainActivity.this);
            c.setDialogResult(new CustomDialog.OnMyDialogResult() {
                @Override
                public void finish(String type, String content) {
                    t.setType(type);
                    t.setContent(content);
                    /* Save to Shared Preferences */
                    SharedPreferences.Editor prefsEditor = textSharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(t);
                    prefsEditor.putString(""+t.getId(), json);
                    prefsEditor.apply();
                }
            });
            c.show();
        }
        return true;
    }
}
