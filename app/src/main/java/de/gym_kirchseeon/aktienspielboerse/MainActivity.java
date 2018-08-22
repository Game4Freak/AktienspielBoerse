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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private JSONObject jObj = new JSONObject();
    private GridLayout gridMain;
    private FloatingActionButton fab;
    private Button scrollUpMain;
    private TextView moneyTxt;
    private TextView sharesTxt;
    private TextView sumTxt;
    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private RecyclerView recyclerShares;
    private CompanyAdapter cAdapter;
    private NestedScrollView scrollMain;
    private SwipeRefreshLayout refreshMain;
    private float money;
    private float sharesWorth;
    private boolean isRefreshing;

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

        JSONArray jA = new JSONArray();
        JSONObject jObjTemp = new JSONObject();
        try {
            jObjTemp.put(getResources().getString(R.string.nameCompany), "Tesla Inc.");
            jObjTemp.put(getResources().getString(R.string.worthCompany), 94.47);
            jObjTemp.put(getResources().getString(R.string.changeCompany), +0.3);
            jObjTemp.put(getResources().getString(R.string.countCompany), 8);
            jA.put(jObjTemp);

            jObjTemp = new JSONObject();
            jObjTemp.put(getResources().getString(R.string.nameCompany), "BMW AG");
            jObjTemp.put(getResources().getString(R.string.worthCompany), 238.24);
            jObjTemp.put(getResources().getString(R.string.changeCompany), -0.7);
            jObjTemp.put(getResources().getString(R.string.countCompany), 5);
            jA.put(jObjTemp);

            jObjTemp = new JSONObject();
            jObjTemp.put(getResources().getString(R.string.nameCompany), "Nintendo Co. Ltd.");
            jObjTemp.put(getResources().getString(R.string.worthCompany), 147.23);
            jObjTemp.put(getResources().getString(R.string.changeCompany), +2.3);
            jObjTemp.put(getResources().getString(R.string.countCompany), 26);
            jA.put(jObjTemp);

            jObj.put(getResources().getString(R.string.company), jA);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        cAdapter = new CompanyAdapter(this, CompanyAdapter.MAIN_ACTIVITY, jObj);

        RecyclerView.LayoutManager cLayoutManager = new CustomGridLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerShares.setLayoutManager(cLayoutManager);
        recyclerShares.setItemAnimator(new DefaultItemAnimator());
        recyclerShares.setAdapter(cAdapter);

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
                scrollMain.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollMain.fling(0);
                        scrollMain.smoothScrollTo(0, 0);
                    }
                });
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

        try {
            JSONArray shares = jObj.getJSONArray(getResources().getString(R.string.company));
            for (int i = 0; i < shares.length(); i++) {
                shareWorth += shares.getJSONObject(i).getDouble("worth") * shares.getJSONObject(i).getDouble("count");
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showPopup(MenuItem item) {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.sortSharesMain));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sort_by, popup.getMenu());
        popup.show();
    }

    public void onShareClick(View v) {
        TextView nameTxt = v.findViewById(R.id.companySharesNameTxt);
        String name = nameTxt.getText().toString();

        TextView worthTxt = v.findViewById(R.id.shareMultiplicationTxt);
        String worthStr = worthTxt.getText().toString().split(" x ")[1];
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        double worth = 0;
        try {
            Number number = format.parse(worthStr);
            worth = number.doubleValue();
        } catch (ParseException e) {
            Log.e("ParseException", e.getMessage());
        }

        JSONObject company = cAdapter.getCompany(name, worth);

        if (company != null) {
            Intent i = new Intent(MainActivity.this, CompanyActivity.class);
            i.putExtra("company", company.toString());
            startActivity(i);
        } else {
            Log.e("CompanyError", "Company is null");
        }
    }

    public void sortAlphabetically(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_ALPHABETICALLY);
    }

    public void sortAlphabeticallyReverse(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_ALPHABETICALLY_REVERSE);
    }

    public void sortByWorth(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_WORTH);
    }

    public void sortByWorthReverse(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_WORTH_REVERSE);
    }

    public void sortByCount(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_COUNT);
    }

    public void sortByCountReverse(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_COUNT_REVERSE);
    }
}