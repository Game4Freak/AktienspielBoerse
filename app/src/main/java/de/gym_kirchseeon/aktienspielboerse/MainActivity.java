package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<JSONObject> jObjList = new ArrayList<>();
    private GridLayout gridMain;
    private FloatingActionButton fab;
    private Button scrollUpMain;
    private TextView moneyTxt;
    private TextView sharesTxt;
    private TextView sumTxt;
    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private RecyclerView recyclerShares;
    private SharesAdapter sAdapter;
    private NestedScrollView scrollMain;
    private SwipeRefreshLayout refreshMain;
    private float money;
    private float sharesWorth;
    private boolean isRefreshing;
    private JSONObject jObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.abMain);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        gridMain = findViewById(R.id.gridMoney);
        fab = findViewById(R.id.fabMain);
        scrollUpMain = findViewById(R.id.scrollUpMain);
        moneyTxt = findViewById(R.id.moneyTxt);
        sharesTxt = findViewById(R.id.sharesTxt);
        sumTxt = findViewById(R.id.sumTxt);
        toolbarLayout = findViewById(R.id.abMainLayout);
        recyclerShares = findViewById(R.id.recyclerShares);
        scrollMain = findViewById(R.id.scrollMain);
        scrollMain.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        refreshMain = findViewById(R.id.refreshMain);
        isRefreshing = false;

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

        scrollMain.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scroll = scrollMain.getScrollY();
                if (scroll == 0) {
                    toolbarLayout.setElevation(0);
                } else {
                    toolbarLayout.setElevation(8);
                }

                if (scroll < gridMain.getHeight()) {
                    scrollUpMain.setVisibility(View.INVISIBLE);
                } else {
                    scrollUpMain.setVisibility(View.VISIBLE);
                }
            }
        });

        refreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                refresh();
            }
        });

        scrollUpMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollMain.scrollTo(0, 0);
            }
        });

        refresh();
    }

    private void refresh() {
        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();

        if (sharedPref.getBoolean("isFirstRun", true)) {
            sharedPrefEdit.putBoolean("isFirstRun", false);
            sharedPrefEdit.putFloat(getString(R.string.moneyShared), 5000);
            sharedPrefEdit.apply();
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

        if (isRefreshing) {
            Toast.makeText(MainActivity.this, "Alles neugeladen", Toast.LENGTH_SHORT).show();
            isRefreshing = false;
            refreshMain.setRefreshing(isRefreshing);
        }
    }

    public void onShareClick(View v) {
        Intent i = new Intent(MainActivity.this, CompanyActivity.class);
        try {
            i.putExtra("name", jObj.getString("name"));
            i.putExtra("worth", jObj.getDouble("worth"));
        } catch (JSONException e) {
            i.putExtra("name", "Fehler");
            i.putExtra("worth", 0);
        }
        startActivity(i);
    }
}