package de.gym_kirchseeon.aktienspielboerse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * The activity showing all available companies / shares
 */
public class SharesActivity extends AppCompatActivity {

    private JSONObject jObj = new JSONObject();
    private RecyclerView recyclerCompanies;
    private CompanyAdapter cAdapter;
    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private NestedScrollView scrollShares;
    private SwipeRefreshLayout refreshShares;
    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shares);
        toolbar = findViewById(R.id.abShares);
        toolbar.setTitle("Unternehmen");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarLayout = findViewById(R.id.abSharesLayout);
        recyclerCompanies = findViewById(R.id.recyclerCompanies);
        scrollShares = findViewById(R.id.scrollShares);
        scrollShares.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        refreshShares = findViewById(R.id.refreshShares);
        isRefreshing = false;

        JSONArray jA = new JSONArray();
        JSONObject jObjTemp = new JSONObject();
        try {
            jObjTemp.put(getResources().getString(R.string.nameCompany), "Tesla Inc.");
            jObjTemp.put(getResources().getString(R.string.currentWorthCompany), 94.47);
            jObjTemp.put(getResources().getString(R.string.changeCompany), +0.3);
            jObjTemp.put(getResources().getString(R.string.countCompany), 8);
            jA.put(jObjTemp);

            jObjTemp = new JSONObject();
            jObjTemp.put(getResources().getString(R.string.nameCompany), "BMW AG");
            jObjTemp.put(getResources().getString(R.string.currentWorthCompany), 238.24);
            jObjTemp.put(getResources().getString(R.string.changeCompany), -0.7);
            jObjTemp.put(getResources().getString(R.string.countCompany), 5);
            jA.put(jObjTemp);

            jObjTemp = new JSONObject();
            jObjTemp.put(getResources().getString(R.string.nameCompany), "Nintendo Co. Ltd.");
            jObjTemp.put(getResources().getString(R.string.currentWorthCompany), 147.23);
            jObjTemp.put(getResources().getString(R.string.changeCompany), +2.3);
            jObjTemp.put(getResources().getString(R.string.countCompany), 26);
            jA.put(jObjTemp);

            jObjTemp = new JSONObject();
            jObjTemp.put(getResources().getString(R.string.nameCompany), "VW AG");
            jObjTemp.put(getResources().getString(R.string.currentWorthCompany), 152.98);
            jObjTemp.put(getResources().getString(R.string.changeCompany), -3.1);
            jObjTemp.put(getResources().getString(R.string.countCompany), 0);
            jA.put(jObjTemp);

            jObj.put(getResources().getString(R.string.company), jA);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        cAdapter = new CompanyAdapter(this, CompanyAdapter.SHARES_ACTIVITY, jObj);

        RecyclerView.LayoutManager cLayoutManager = new CustomGridLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerCompanies.setLayoutManager(cLayoutManager);
        recyclerCompanies.setItemAnimator(new DefaultItemAnimator());
        recyclerCompanies.setAdapter(cAdapter);

        scrollShares.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scroll = scrollShares.getScrollY();
                if (scroll == 0) {
                    toolbarLayout.setElevation(0);
                } else {
                    toolbarLayout.setElevation(8);
                }
            }
        });

        refreshShares.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                refresh();
            }
        });


        refresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_shares, menu);
        MenuItem searchShares = menu.findItem(R.id.searchShares);
        SearchView searchView = (SearchView) searchShares.getActionView();
        searchView.setQueryHint("Unternehmen suchen");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                cAdapter.getFilter().filter(text);
                return false;
            }
        });

        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Shows the sorting dialog
     */
    public void showPopup(MenuItem item) {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.sortShares));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sort_by, popup.getMenu());
        popup.show();
    }

    /**
     * Shows details about the pressed company / share
     */
    public void onCompanyClick(View v) {
        TextView nameTxt = v.findViewById(R.id.companyNameTxt);
        String name = nameTxt.getText().toString();

        TextView worthTxt = v.findViewById(R.id.shareWorthTxt);
        String worthStr = worthTxt.getText().toString().split("€")[0];
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
            Intent i = new Intent(SharesActivity.this, CompanyActivity.class);
            i.putExtra("company", company.toString());
            startActivity(i);
        } else {
            Log.e("CompanyError", "Company is null");
        }
    }

    /**
     * First Run: Sets money to start with
     * Refreshes shares and money
     * non-functional
     */
    public void refresh() {
        //TODO: Neu laden

        if (isRefreshing) {
            Toast.makeText(this, "Alles neugeladen", Toast.LENGTH_SHORT).show();
            isRefreshing = false;
            refreshShares.setRefreshing(isRefreshing);
        }
    }

    /**
     * Sorts alphabetically
     */
    public void sortAlphabetically(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_ALPHABETICALLY);
    }

    /**
     * Sorts reverse alphabetically
     */
    public void sortAlphabeticallyReverse(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_ALPHABETICALLY_REVERSE);
    }

    /**
     * Sorts by the highest worth
     */
    public void sortByWorth(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_WORTH);
    }

    /**
     * Sorts by the lowest worth
     */
    public void sortByWorthReverse(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_WORTH_REVERSE);
    }

    /**
     * Sorts by the highest count of bought shares
     */
    public void sortByCount(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_COUNT);
    }

    /**
     * Sorts by the lowest count of bought shares
     */
    public void sortByCountReverse(MenuItem item) {
        cAdapter.sort(CompanyAdapter.SORT_BY_COUNT_REVERSE);
    }
}