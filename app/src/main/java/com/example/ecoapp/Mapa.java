package com.example.ecoapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ecoapp.DatabaseHelper;
import com.example.ecoapp.RecyclingPoint;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Mapa extends AppCompatActivity {

    private MapView map;
    private Button btn_atras;
    private static final int REQUEST_PERMISSIONS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        btn_atras = findViewById(R.id.button);

        // Configuración de osmdroid
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Solicitud de permisos
        requestPermissionsIfNecessary(new String[] {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        });

        // Inicialización del mapa
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Centrar el mapa en Anserma, Caldas
        GeoPoint startPoint = new GeoPoint(5.2309, -75.7877);
        map.getController().setZoom(16.0);
        map.getController().setCenter(startPoint);

        // Cargar y mostrar los puntos de reciclaje
        addRecyclingPointsFromDatabase();

        // Configuración del botón para volver
        OnClickButtonsListener();
    }

    public void OnClickButtonsListener() {
        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mapa.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addRecyclingPointsFromDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<RecyclingPoint> points = dbHelper.getAllRecyclingPoints();
        for (RecyclingPoint point : points) {
            GeoPoint geoPoint = new GeoPoint(point.getLatitude(), point.getLongitude());
            addMarker(geoPoint, point.getName(), point.getAddress());
        }
        dbHelper.close();
    }

    private void addMarker(GeoPoint geoPoint, String title, String snippet) {
        Marker marker = new Marker(map);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        map.getOverlays().add(marker);
        map.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_CODE);
        }
    }
}