package de.gym_kirchseeon.aktienspielboerse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

public class CompanyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout toolbarLayout;
    private ScrollView scrollCompany;

    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        Intent i = getIntent();
        company = i.getStringExtra("company");

        toolbar = findViewById(R.id.abCompany);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle(company);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarLayout = findViewById(R.id.abCompanyLayout);
        scrollCompany = findViewById(R.id.scrollCompany);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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