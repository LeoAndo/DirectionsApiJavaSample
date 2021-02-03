package com.template.directionsapiforjavasample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float ZOOM_SIZE = 14f;
    private static final float POLYLINE_WIDTH = 12f;
    private GoogleMap mMap;
    private Polyline polyline;
    private static final int overview = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 経路を表示.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 別スレッドでDirections APIをコールする.
                com.google.maps.model.LatLng fromTokyo = new com.google.maps.model.LatLng(35.68183, 139.76715);
                com.google.maps.model.LatLng toKanda = new com.google.maps.model.LatLng(35.69274, 139.77114);
                DirectionsApiHelper directionsApiHelper = new DirectionsApiHelper();
                DirectionsResult result = directionsApiHelper.execute(MapsActivity.this, fromTokyo, toKanda);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(MapsActivity.class.getSimpleName(), "result: " + result);
                        updatePolyline(result, googleMap);
                        // カメラ移動.
                        moveCamera();
                    }
                });
            }
        }).start();
    }

    private void moveCamera() {
        // Add a marker in Sydney and move the camera
        com.google.android.gms.maps.model.LatLng tokyo = new com.google.android.gms.maps.model.LatLng(35.68183, 139.76715);
        mMap.addMarker(new MarkerOptions().position(tokyo).title("Marker in Tokyo"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(tokyo));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, ZOOM_SIZE));
    }

    private void updatePolyline(DirectionsResult directionsResult, GoogleMap googleMap) {
        removePolyline();

        if (googleMap == null) {
            Toast.makeText(this, "mMap == null", Toast.LENGTH_SHORT).show();
            return;
        }
        addPolyline(directionsResult, googleMap);
    }

    // 線を消す.
    private void removePolyline() {
        if (mMap != null && polyline != null) {
            polyline.remove();
        }
    }

    // 線を引く
    private void addPolyline(DirectionsResult directionsResult, GoogleMap map) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(POLYLINE_WIDTH);
        // ARGB32bit形式.
        int colorPrimary = ContextCompat.getColor(this, R.color.map_polyline_stroke);
        polylineOptions.color(colorPrimary);

        List<LatLng> decodedPath = com.google.maps.android.PolyUtil.decode(directionsResult.routes[overview].overviewPolyline.getEncodedPath());
        polyline = map.addPolyline(polylineOptions.addAll(decodedPath));
    }

    // 線を引く
    private void addPolyline2(DirectionsResult directionsResult, GoogleMap map) {
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        DirectionsRoute route = directionsResult.routes[0];
        PolylineOptions polylineOptions = new PolylineOptions();
        for (com.google.maps.model.LatLng latLng : route.overviewPolyline.decodePath()) {
            polylineOptions.add(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
            bounds.include(new com.google.android.gms.maps.model.LatLng(latLng.lat, latLng.lng));
        }
        polylineOptions.width(POLYLINE_WIDTH);
        int colorPrimary = ContextCompat.getColor(this, R.color.map_polyline_stroke);
        polylineOptions.color(colorPrimary);

        polyline = mMap.addPolyline(polylineOptions);
    }
}