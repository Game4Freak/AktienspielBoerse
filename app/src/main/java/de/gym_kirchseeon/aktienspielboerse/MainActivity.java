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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView moneyTxt;
    private TextView sharesTxt;
    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private RecyclerView recyclerShares;
    private SharesAdapter sAdapter;
    private ScrollView scrollMain;

    private float money;
    private float sharesWorth;

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
        toolbarLayout = findViewById(R.id.abMainLayout);
        recyclerShares = findViewById(R.id.recyclerShares);
        scrollMain = findViewById(R.id.scrollMain);
        scrollMain.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        sAdapter = new SharesAdapter();
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
        money = sharedPref.getFloat(getString(R.string.moneyShared), 0);
        sharesWorth = sharedPref.getFloat(getString(R.string.sharesWorthShared), 0);

        moneyTxt.setText(money + "€");
        sharesTxt.setText(sharesWorth + "€");
    }

    public void onShareClick(View v) {
        Intent i = new Intent(MainActivity.this, CompanyActivity.class);
        i.putExtra("company", "Unternehmen");
        startActivity(i);
    }
}