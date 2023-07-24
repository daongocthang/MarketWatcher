package com.standalone.tradingplan.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.standalone.droid.requests.HttpVolley;
import com.standalone.tradingplan.models.StockInfo;
import com.standalone.tradingplan.models.StockRealTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Broker {
    public static final String TAG = Broker.class.getSimpleName();

    public static void fetchStockInfo(Context context, OnResponseListener<List<StockInfo>> responseListener) {
        String url = "https://iboard.ssi.com.vn/dchart/api/1.1/defaultAllStocks";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Gson gson = new Gson();
                    StockInfo[] stockInfoArray = gson.fromJson(String.valueOf(data), StockInfo[].class);
                    responseListener.onResponse(Arrays.stream(stockInfoArray).collect(Collectors.toList()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "JsonObjectRequest onErrorResponse: " + error.getMessage());
            }
        });

        HttpVolley.getInstance(context).getRequestQueue().add(request);
    }


    public static void fetchStockRealTimes(Context context, List<String> stockNoList, OnResponseListener<List<StockRealTime>> responseListener) {
        String url = "https://wgateway-iboard.ssi.com.vn/graphql";

        String[] params = {
                "stockNo",
                "stockSymbol",
                "ceiling",
                "floor",
                "refPrice",
                "matchedPrice",
                "lastMatchedPrice",
                "matchedVolume",
                "highest",
                "lowest"
        };

        StringJoiner joiner = new StringJoiner(",");
        for (String s : stockNoList) {
            joiner.add(String.format("'%s'", s));
        }

        String body = "{ " +
                "'operationName': 'stockRealtimesByIds', " +
                "'variables': { " +
                "'ids': [" + joiner.toString() + "] " +
                "}, " +
                "'query': 'query stockRealtimesByIds($ids: [String!]) {\\n stockRealtimesByIds(ids: $ids) {\\n " + String.join("\\n ", params) + " }\\n}\\n'" +
                "}";

        try {
            JSONObject jsonBody = new JSONObject(body.replace("'", "\""));
            Log.i(TAG, jsonBody.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray data = response.getJSONObject("data").getJSONArray("stockRealtimesByIds");
                        Log.e(TAG, "StockRealTIme: " + data.toString());
                        StockRealTime[] stockRealTimeArray = new Gson().fromJson(String.valueOf(data), StockRealTime[].class);
                        responseListener.onResponse(Arrays.stream(stockRealTimeArray).collect(Collectors.toList()));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "JsonObjectRequest onErrorResponse: " + error.getMessage());
                    responseListener.onError();
                }
            });

            request.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            HttpVolley.getInstance(context).getRequestQueue().add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnResponseListener<T> {
        void onResponse(T t);

        void onError();
    }
}
