package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.gson.Gson;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.StockApi;
import com.udacity.stockhawk.data.StockApiClient;
import com.udacity.stockhawk.data.model.Dataset;
import com.udacity.stockhawk.data.model.ErrorResponse;
import com.udacity.stockhawk.data.model.Stock;
import com.udacity.stockhawk.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import timber.log.Timber;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private static final String QUANDL_ROOT = "https://www.quandl.com/api/v3/datasets/WIKI/";

    private static final OkHttpClient client = new OkHttpClient();
    private static final LocalDate startDate = LocalDate.now().minus(YEARS_OF_HISTORY, ChronoUnit.YEARS);
    private static final LocalDate endDate = LocalDate.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static StringBuilder historyBuilder = new StringBuilder();

    private QuoteSyncJob() {
    }

    static HttpUrl createQuery(String symbol) {

        HttpUrl.Builder httpUrl = HttpUrl.parse(QUANDL_ROOT + symbol + ".json").newBuilder();
        httpUrl.addQueryParameter("column_index", "4")  //closing price
                .addQueryParameter("start_date", formatter.format(startDate))
                .addQueryParameter("end_date", formatter.format(endDate));
        return httpUrl.build();
    }

    static ContentValues processStock(Dataset dataset) {
        String stockSymbol = dataset.getDatasetCode();
        List<List<String>> historicData = dataset.getData();

        double price = Double.valueOf(historicData.get(0).get(1));
        double priceBefore = Double.valueOf(historicData.get(1).get(1));
        double change = price - priceBefore;
        double percentChange = 100 * change / priceBefore;

        for (int i = 0; i < historicData.size(); i++) {
            List<String> strings = historicData.get(i);
            String date = strings.get(0);
            Double currentPrice = Double.valueOf(strings.get(1));
            // Append date
            historyBuilder.append(date);
            historyBuilder.append(", ");
            // Append close
            historyBuilder.append(currentPrice);
            historyBuilder.append("\n");
        }

        ContentValues quoteCV = new ContentValues();
        quoteCV.put(Contract.Quote.COLUMN_SYMBOL, stockSymbol);
        quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
        quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
        quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
        quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

        return quoteCV;
    }

    static ContentValues processStock(JSONObject jsonObject) throws JSONException {

        String stockSymbol = jsonObject.getString("dataset_code");

        JSONArray historicData = jsonObject.getJSONArray("data");

        double price = historicData.getJSONArray(0).getDouble(1);
        double change = price - historicData.getJSONArray(1).getDouble(1);
        double percentChange = 100 * ((price - historicData.getJSONArray(1).getDouble(1)) / historicData.getJSONArray(1).getDouble(1));

        historyBuilder = new StringBuilder();

        for (int i = 0; i < historicData.length(); i++) {
            JSONArray array = historicData.getJSONArray(i);
            // Append date
            historyBuilder.append(array.get(0));
            historyBuilder.append(", ");
            // Append close
            historyBuilder.append(array.getDouble(1));
            historyBuilder.append("\n");
        }

        ContentValues quoteCV = new ContentValues();
        quoteCV.put(Contract.Quote.COLUMN_SYMBOL, stockSymbol);
        quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
        quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
        quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
        quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

        return quoteCV;
    }

    static void getQuotes(final Context context) {

        Toast toast;

        Timber.d("Running sync job");

        historyBuilder = new StringBuilder();

        try {

            Set<String> stockPref = PrefUtils.getStocks(context);

            int index = 4;
            String start = formatter.format(startDate);
            String end = formatter.format(endDate);

            for (String stock : stockPref) {
                /*Request request = new Request.Builder()
                        .url(createQuery(stock)).build();*/

                StockApi stockApiClient = StockApiClient.getStockApiClient();
                stockApiClient.groupList(stock, index, start, end)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Stock>() {
                            @Override
                            public void onSuccess(Stock stock) {
                                ContentValues quotes = processStock(stock.getDataset());
                                context.getContentResolver().insert(Contract.Quote.URI, quotes);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    HttpException ex = (HttpException) e;
                                    if (ex.code() == 404) {
                                        PrefUtils.removeStock(context, stock);
                                        Toast.makeText(context, String.format(context.getString(R.string.toast_stock_unavailable), stock), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                Timber.e(e);
                            }
                        });
            }

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            context.sendBroadcast(dataUpdatedIntent);

        } catch (Exception exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }

    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);

    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));

            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }


}
