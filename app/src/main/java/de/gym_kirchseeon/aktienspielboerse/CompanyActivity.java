package de.gym_kirchseeon.aktienspielboerse;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class CompanyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private FloatingActionButton fab;
    private LinearLayout backgroundChart;
    private Button scrollUpCompany;
    private NestedScrollView scrollCompany;
    private SwipeRefreshLayout refreshCompany;
    private TextView worthCompanyTxt;
    private TextView companyNameBuyTxt;
    private TextView companyWorthBuyTxt;
    private NumberPicker buyCountPicker;
    private Button buyBtn;

    private String name;
    private double worth;
    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        worth = i.getDoubleExtra("worth", 0);

        toolbar = findViewById(R.id.abCompany);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        scrollUpCompany = findViewById(R.id.scrollUpCompany);
        fab = findViewById(R.id.fabBuy);
        backgroundChart = findViewById(R.id.backgroundChart);
        toolbarLayout = findViewById(R.id.abCompanyLayout);
        scrollCompany = findViewById(R.id.scrollCompany);
        refreshCompany = findViewById(R.id.refreshCompany);
        worthCompanyTxt = findViewById(R.id.worthCompanyTxt);
        isRefreshing = false;

        worthCompanyTxt.setText(String.format("%.2f€", worth));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyDialog();
            }
        });

        scrollCompany.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scroll = scrollCompany.getScrollY();
                if (scroll == 0) {
                    toolbarLayout.setElevation(0);
                } else {
                    toolbarLayout.setElevation(8);
                }

                if (scroll < backgroundChart.getHeight()) {
                    scrollUpCompany.setVisibility(View.INVISIBLE);
                } else {
                    scrollUpCompany.setVisibility(View.VISIBLE);
                }
            }
        });

        refreshCompany.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                refresh();
            }
        });

        scrollUpCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollCompany.scrollTo(0, 0);
            }
        });

        refresh();
    }

    private void buy(final View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Rückgängig", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "Rückgängig gemacht", Snackbar.LENGTH_SHORT).show();
                        //TODO: Rückgängig
                    }
                }).show();
    }

    private void showBuyDialog() {
        AlertDialog.Builder dialogBuyBuilder = new AlertDialog.Builder(this);
        LayoutInflater lInflater = getLayoutInflater();
        final View layout = lInflater.inflate(R.layout.buy_popup, null);
        dialogBuyBuilder.setView(layout);
        final AlertDialog dialogBuy = dialogBuyBuilder.create();

        companyNameBuyTxt = layout.findViewById(R.id.companyNameBuyTxt);
        companyWorthBuyTxt = layout.findViewById(R.id.companyWorthBuyTxt);
        buyCountPicker = layout.findViewById(R.id.buyCountPicker);
        buyBtn = layout.findViewById(R.id.buyBtn);

        companyNameBuyTxt.setText(name);
        companyWorthBuyTxt.setText(String.format("%.2f€", worth));
        buyCountPicker.setMinValue(1);
        buyCountPicker.setMaxValue(100);

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countBuy = buyCountPicker.getValue();
                //TODO: Kaufen
                dialogBuy.dismiss();
                buy(findViewById(R.id.fabBuy), String.format("Du hast %d Aktien von %s für %.2f€ gekauft", countBuy, name, worth * countBuy));
            }
        });
        dialogBuy.show();
    }

    private void refresh() {
        //TODO: Neu laden

        if (isRefreshing) {
            Toast.makeText(this, "Alles neugeladen", Toast.LENGTH_SHORT).show();
            isRefreshing = false;
            refreshCompany.setRefreshing(isRefreshing);
        }
    }
}