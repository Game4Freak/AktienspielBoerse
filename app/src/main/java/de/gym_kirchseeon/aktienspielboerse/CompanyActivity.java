package de.gym_kirchseeon.aktienspielboerse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CompanyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private FloatingActionButton fab;
    private ScrollView scrollCompany;
    private TextView worthCompanyTxt;

    private String company;
    private double worth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Intent i = getIntent();
        company = i.getStringExtra("company");
        worth = i.getDoubleExtra("worth", 0);

        toolbar = findViewById(R.id.abCompany);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle(company);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab = findViewById(R.id.fabBuy);
        toolbarLayout = findViewById(R.id.abCompanyLayout);
        scrollCompany = findViewById(R.id.scrollCompany);
        worthCompanyTxt = findViewById(R.id.worthCompanyTxt);

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
                Toast t = Toast.makeText(CompanyActivity.this, String.format("Du hast eine Aktie von %s für %.2f€ gekauft!", company, worth), Toast.LENGTH_LONG);
                t.show();
                //TODO: Kaufen!
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
            }
        });
    }
}