package com.standalone.tradingplan.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.standalone.droid.requests.HttpVolley;
import com.standalone.tradingplan.models.StockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Broker {
    private static final String TAG = Broker.class.getSimpleName();

    public static void fetchStockInfo(Context context, OnResponseListener<StockInfo[]> responseListener) {
        String url = "https://iboard.ssi.com.vn/dchart/api/1.1/defaultAllStocks";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
                    Gson gson = new Gson();
                    StockInfo[] stockInfoArray = gson.fromJson(String.valueOf(data), StockInfo[].class);
                    StockInfo[] sanityArray = (StockInfo[]) Arrays.stream(stockInfoArray).filter(s -> s.type.equals("s")).toArray();
                    responseListener.onResponse(sanityArray);

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

    public interface OnResponseListener<T> {
        void onResponse(T t);

        void onError();
    }
}
