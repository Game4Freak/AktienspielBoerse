package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<JSONObject> jObjList = new ArrayList<>();
    private FloatingActionButton fab;
    private TextView moneyTxt;
    private TextView sharesTxt;
    private TextView sumTxt;
    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private RecyclerView recyclerShares;
    private SharesAdapter sAdapter;
    private ScrollView scrollMain;
    private float money;
    private float sharesWorth;
    private JSONObject jObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.abMain);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fabMain);
        moneyTxt = findViewById(R.id.moneyTxt);
        sharesTxt = findViewById(R.id.sharesTxt);
        sumTxt = findViewById(R.id.sumTxt);
        toolbarLayout = findViewById(R.id.abMainLayout);
        recyclerShares = findViewById(R.id.recyclerShares);
        scrollMain = findViewById(R.id.scrollMain);
        scrollMain.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        try {
            jObj.put("name", "BMW");
            jObj.put("worth", 143.23);
            jObj.put("count", 5);
            jObj.put("change", -1.5);

            jObjList.add(jObj);
        } catch (JSONException e) {
        }

        sAdapter = new SharesAdapter(jObjList);
        RecyclerView.LayoutManager cLayoutManager = new CustomGridLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerShares.setLayoutManager(cLayoutManager);
        recyclerShares.setItemAnimator(new DefaultItemAnimator());
        recyclerShares.setAdapter(sAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SharesActivity.class);
                startActivity(intent);
            }
        });

        toolbarLayout.setElevation(0);
        scrollMain.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scroll = scrollMain.getScrollY();
                if (scroll == 0) {
                    toolbarLayout.setElevation(0);
                } else {
                    toolbarLayout.setElevation(8);
                }
            }
        });

        refresh(MainActivity.this);
    }

    private void refresh(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        if (sharedPref.getBoolean("isFirstRun", true)) {
            sharedPrefEdit.putBoolean("isFirstRun", false);
            sharedPrefEdit.putFloat(getString(R.string.moneyShared), 5000);
            sharedPrefEdit.commit();
        }

        float shareWorth = 0;

        for (int i = 0; i < jObjList.size(); i++) {
            try {
                shareWorth += jObjList.get(i).getDouble("worth") * jObjList.get(i).getDouble("count");
            } catch (JSONException e) {
            }
        }
        sharedPrefEdit.putFloat(getString(R.string.sharesWorthShared), shareWorth);
        sharedPrefEdit.commit();

        money = sharedPref.getFloat(getString(R.string.moneyShared), 0);
        sharesWorth = sharedPref.getFloat(getString(R.string.sharesWorthShared), 0);

        moneyTxt.setText(String.format("%.2f€", money));
        sharesTxt.setText(String.format("%.2f€", sharesWorth));
        sumTxt.setText(String.format("%.2f€", money + sharesWorth));
    }

    public void onShareClick(View v) {
        Intent i = new Intent(MainActivity.this, CompanyActivity.class);
        try {
            i.putExtra("company", jObj.getString("name"));
            i.putExtra("worth", jObj.getDouble("worth"));
        } catch (JSONException e) {
            i.putExtra("company", "Fehler");
            i.putExtra("worth", 0);
        }
        startActivity(i);
    }

    public void refresh(MenuItem item) {
        refresh(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }
}