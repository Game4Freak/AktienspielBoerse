package de.gym_kirchseeon.aktienspielboerse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

public class SharesActivity extends AppCompatActivity {

    private RecyclerView recyclerCompanies;
    private CompanyAdapter cAdapter;
    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private ScrollView scrollShares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shares);
        toolbar = findViewById(R.id.abShares);
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

        cAdapter = new CompanyAdapter();
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_shares, menu);
        return true;
    }

    public boolean showPopup(MenuItem item) {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.sortShares));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sort_by, popup.getMenu());
        popup.show();
        return true;
    }

    public void onCompanyClick(View v) {
        Intent i = new Intent(SharesActivity.this, CompanyActivity.class);
        i.putExtra("company", "Unternehmen");
        startActivity(i);
    }
}