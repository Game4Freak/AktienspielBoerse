package de.gym_kirchseeon.aktienspielboerse;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The activity showing details about a company
 */
public class CompanyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private FloatingActionButton fab;
    private GridLayout backgroundChart;
    private Button scrollUpCompany;
    private NestedScrollView scrollCompany;
    private SwipeRefreshLayout refreshCompany;
    private TextView worthCompanyTxt;
    private TextView countCompanyTxt;
    private TextView changeCompanyTxt;
    private TextView companyNameBuyTxt;
    private TextView companyWorthBuyTxt;
    private NumberPicker buyCountPicker;
    private Button buyBtn;

    private String keyName;
    private String keyWorth;
    private String keyChange;
    private String keyCount;

    private JSONObject company;
    private String name;
    private double worth;
    private double change;
    private int count;
    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        keyName = getResources().getString(R.string.nameCompany);
        keyWorth = getResources().getString(R.string.worthCompany);
        keyChange = getResources().getString(R.string.changeCompany);
        keyCount = getResources().getString(R.string.countCompany);

        try {
            Intent i = getIntent();
            company = new JSONObject(i.getStringExtra("company"));
            name = company.getString(keyName);
            worth = company.getDouble(keyWorth);
            change = company.getDouble(keyChange);
            count = company.getInt(keyCount);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

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
        countCompanyTxt = findViewById(R.id.countCompanyTxt);
        changeCompanyTxt = findViewById(R.id.changeCompanyTxt);
        isRefreshing = false;

        worthCompanyTxt.setText(String.format("%.2f€", worth));
        countCompanyTxt.setText(String.format("%d", count));
        changeCompanyTxt.setText(String.format("%+.1f%%", change));
        if (change > 0) {
            changeCompanyTxt.setTextColor(Color.parseColor("#00c853"));
        } else if (change < 0) {
            changeCompanyTxt.setTextColor(Color.parseColor("#d50000"));
        }

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
                scrollCompany.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollCompany.fling(0);
                        scrollCompany.smoothScrollTo(0, 0);
                    }
                });
            }
        });

        refresh();
    }

    /**
     * Shows a Snackbar after buying shares
     *
     * @param message The message being shown in the Snackbar
     */
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

    /**
     * Shows a popup with a NumberPicker to buy shares
     */
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

    /**
     * Refreshes shares
     * non-functional
     */
    private void refresh() {
        //TODO: Neu laden

        if (isRefreshing) {
            Toast.makeText(this, "Alles neugeladen", Toast.LENGTH_SHORT).show();
            isRefreshing = false;
            refreshCompany.setRefreshing(isRefreshing);
        }
    }
}