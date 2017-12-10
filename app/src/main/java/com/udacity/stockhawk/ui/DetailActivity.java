package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_SYMBOL = "ExtraSymbol";
    private static final String EXTRA_HISTORY = "ExtraHistory";
    @BindView(R.id.cubiclinechart)
    ValueLineChart mCubicValueLineChart;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String mSymbol;


    public static void start(Context context, String symbol, String historicalData){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_SYMBOL, symbol);
        intent.putExtra(EXTRA_HISTORY, historicalData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mSymbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        String historicalData = getIntent().getStringExtra(EXTRA_HISTORY);
        String[] history = historicalData.split("\n");

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        tvTitle.setText(mSymbol);
        List<String> historyList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            historyList.add(history[i]);
        }
        for (int i = historyList.size() - 1; i >= 0; i--) {
            String[] data = historyList.get(i).split(",");
            String tahun = convertDate(data[0]);
            Float nilai = Float.valueOf(data[1]);
            series.addPoint(new ValueLinePoint(tahun, nilai));
        }
        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();

    }

    private String convertDate(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = simpleDateFormat.parse(date);
            return new SimpleDateFormat("yy/MM/dd").format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
