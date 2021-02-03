package com.template.directionsapiforjavasample;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.io.IOException;
import java.util.Locale;

public class DirectionsApiHelper {

    /**
     * 経路APIを実行する.
     *
     * @param context     コンテキスト
     * @param origin      出発地点
     * @param destination 到着地点
     * @return 取得成功: [com.google.maps.model.DirectionsResult] 失敗: null
     */
    @Nullable
    @WorkerThread
    public DirectionsResult execute(Context context, LatLng origin, LatLng destination) {
        // Mapキーの取得.
        GeoApiContext apiContext = new GeoApiContext.Builder()
                .apiKey(context.getString(R.string.google_maps_key)).build();

        // API実行.
        DirectionsResult result;
        try {
            result = DirectionsApi
                    .newRequest(apiContext)
                    .mode(TravelMode.WALKING)
                    .units(Unit.METRIC)
                    .language(Locale.JAPAN.getLanguage())
                    .origin(origin.lat + "," + origin.lng)
                    .destination(destination.lat + "," + destination.lng)
                    .await();
        } catch (ApiException | InterruptedException | IOException e) {
            Log.e(DirectionsApiHelper.class.getSimpleName(), "error: " + e.getLocalizedMessage());
            return null;
        }
        Log.d(DirectionsApiHelper.class.getSimpleName(), "result: "+ result);
        return result;
    }
}
