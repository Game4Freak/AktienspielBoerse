package de.gym_kirchseeon.aktienspielboerse;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The activity showing details about a company
 */
public class CompanyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private FloatingActionButton fabCancel;
    private FloatingActionButton fabBuy;
    private FloatingActionButton fabSell;
    private Spinner spinnerScale;
    private GridLayout backgroundChart;
    private Button scrollUpCompany;
    private NestedScrollView scrollCompany;
    private SwipeRefreshLayout refreshCompany;
    private TextView worthCompanyTxt;
    private TextView countCompanyTxt;
    private TextView changeCompanyTxt;
    private TextView companyNameBuyTxt;
    private TextView companyWorthBuyTxt;
    private TextView companyDescriptionTxt;
    private NumberPicker buyCountPicker;
    private Button buyBtn;
    private GraphView graphView;

    private String keyName;
    private String keyWorth;
    private String keyChange;
    private String keyCount;
    private String keyCurrentWorth;

    private JSONObject company;
    private String name;
    private double currentWorth;
    private double change;
    private int count;
    private JSONArray worth;

    private boolean isRefreshing;
    private LineGraphSeries<DataPoint> graphSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        keyName = getResources().getString(R.string.nameCompany);
        keyCurrentWorth = getResources().getString(R.string.currentWorthCompany);
        keyChange = getResources().getString(R.string.changeCompany);
        keyCount = getResources().getString(R.string.countCompany);
        keyWorth = getResources().getString(R.string.worthCompany);


        try {
            Intent i = getIntent();
            company = new JSONObject(i.getStringExtra("company"));
            name = company.getString(keyName);
            currentWorth = company.getDouble(keyCurrentWorth);
            change = company.getDouble(keyChange);
            count = company.getInt(keyCount);
            worth = company.getJSONArray(keyWorth);
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
        fab = findViewById(R.id.fabShop);
        fabCancel = findViewById(R.id.fabCancel);
        fabBuy = findViewById(R.id.fabBuy);
        fabSell = findViewById(R.id.fabSell);
        backgroundChart = findViewById(R.id.backgroundChart);
        spinnerScale = findViewById(R.id.spinnerScale);
        scrollCompany = findViewById(R.id.scrollCompany);
        refreshCompany = findViewById(R.id.refreshCompany);
        worthCompanyTxt = findViewById(R.id.worthCompanyTxt);
        countCompanyTxt = findViewById(R.id.countCompanyTxt);
        changeCompanyTxt = findViewById(R.id.changeCompanyTxt);
        companyDescriptionTxt = findViewById(R.id.companyDescriptionTxt);
        graphView = findViewById(R.id.graphShare);
        setupGraph(0, 99);
        isRefreshing = false;

        worthCompanyTxt.setText(String.format("%.2f€", currentWorth));
        countCompanyTxt.setText(String.format("%d", count));
        changeCompanyTxt.setText(String.format("%+.1f%%", change));
        if (change > 0) {
            changeCompanyTxt.setTextColor(Color.parseColor("#00c853"));
        } else if (change < 0) {
            changeCompanyTxt.setTextColor(Color.parseColor("#d50000"));
        }

        WikipediaDownloader wikipediaDownloader = new WikipediaDownloader(CompanyActivity.this);
        wikipediaDownloader.downloadDescription(name, "de", new ServerCallback() {
            @Override
            public void onSuccess(String extract) {
                companyDescriptionTxt.setText(extract);
            }

            @Override
            public void onError() {
                companyDescriptionTxt.setText("Es konnte keine Beschreibung gefunden werden.");
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                fabCancel.show();
                fabCancel.bringToFront();

                fabBuy.show();
                if (count != 0) {
                    fabSell.show();
                }

                fabBuy.animate().setDuration(100).translationY(-getResources().getDimensionPixelOffset(R.dimen.fab_buy));
                fabSell.animate().setDuration(200).translationY(-getResources().getDimensionPixelOffset(R.dimen.fab_sell));
            }
        });

        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        fabBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyDialog();
                cancel();
            }
        });

        fabSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSellDialog();
                cancel();
            }
        });

        scrollCompany.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scroll = scrollCompany.getScrollY();

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

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.scale_spinner_array, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScale.setAdapter(spinnerAdapter);
        spinnerScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Richtige Werte nutzen!
                int index = parent.getSelectedItemPosition();
                switch (index) {
                    case 0:
                        setupGraph(0, 99);
                        break;

                    case 1:
                        setupGraph(25, 99);
                        break;

                    case 2:
                        setupGraph(50, 99);
                        break;

                    case 3:
                        setupGraph(75, 99);
                        break;

                    case 4:
                        setupGraph(98, 99);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        refresh();
    }

    private void buy(String message) {
        snack(message, true);
    }

    private void sell(String message) {
        snack(message, false);
    }

    /**
     * Shows a Snackbar after buying shares
     *
     * @param message The message being shown in the Snackbar
     */
    private void snack(String message, boolean buy) {
        if (buy) {
            Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                    .setAction("Rückgängig", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(fab, "Rückgängig gemacht", Snackbar.LENGTH_SHORT).show();
                            //TODO: Rückgängig
                        }
                    }).show();
        } else {
            Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                    .setAction("Rückgängig", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(fab, "Rückgängig gemacht", Snackbar.LENGTH_SHORT).show();
                            //TODO: Rückgängig
                        }
                    }).show();
        }
    }

    private void showBuyDialog() {
        showDialog(true);
    }

    private void showSellDialog() {
        showDialog(false);
    }

    /**
     * Shows a popup with a NumberPicker to buy shares
     */
    private void showDialog(boolean buy) {
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
        companyWorthBuyTxt.setText(String.format("%.2f€", currentWorth));
        buyCountPicker.setMinValue(1);
        buyCountPicker.setMaxValue(100);

        if (buy) {
            buyBtn.setText("Kaufen");

            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int countBuy = buyCountPicker.getValue();
                    //TODO: Kaufen
                    dialogBuy.dismiss();
                    buy(String.format("Du hast %d Aktien von %s für %.2f€ gekauft", countBuy, name, currentWorth * countBuy));
                }
            });
        } else {
            buyBtn.setText("Verkaufen");

            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int countSell = buyCountPicker.getValue();
                    //TODO: Verkaufen
                    dialogBuy.dismiss();
                    sell(String.format("Du hast %d Aktien von %s für %.2f€ verkauft", countSell, name, currentWorth * countSell));
                }
            });
        }
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

    private void cancel() {
        fab.show();
        fab.bringToFront();

        fabBuy.animate().setDuration(100).translationY(0f);
        fabSell.animate().setDuration(200).translationY(0f)
                .withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                fabBuy.hide();
                                fabSell.hide();
                                fabCancel.hide();
                            }
                        }
                );
    }

    private void setupGraph(int minX, int maxX) {
        graphView.removeAllSeries();


        DataPoint[] dataPoints = new DataPoint[100];
        for (int i = 0; i < worth.length(); i++) {
            try {
                dataPoints[i] = new DataPoint(i, worth.getDouble(i));
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }

        graphSeries = new LineGraphSeries<>(dataPoints);

        double max = dataPoints[0].getY();
        for (int i = 1; i < dataPoints.length; i++) {
            if (dataPoints[i].getY() > max) {
                max = dataPoints[i].getY();
            }
        }


        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(max + 50);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(minX);
        graphView.getViewport().setMaxX(maxX);

        graphView.addSeries(graphSeries);
    }
}