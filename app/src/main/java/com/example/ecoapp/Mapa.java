package com.example.ecoapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class Mapa extends AppCompatActivity {

    private MapView map;
    private Button btn_atras,btn_Agregar;
    
    private ListView lv_lista;
    private ArrayList<RecyclingPoint> ListaPuntos;
    
    private static final int REQUEST_PERMISSIONS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        btn_atras = findViewById(R.id.btn_atras);
        btn_Agregar = findViewById(R.id.Agregar);
        lv_lista = findViewById(R.id.lv_lista);
        

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
        CargarArticulos();
    }

    public void OnClickButtonsListener() {
        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mapa.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btn_Agregar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        Intent intent = new Intent(Mapa.this, Puntos_Reciclaje.class);
                        startActivity(intent);
                    }
            });
       /* lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        RecyclingPoint punto = ListaPuntos.get(position);
                        Toast.makeText(Mapa.this, "Los datos se han guardado correctamente", Toast.LENGTH_SHORT).show();
                        
                        

                    }
            });*/
        
        
    }
    
    public void CargarArticulos(){
        DatabaseHelper adminBD = new DatabaseHelper
                (this);
        SQLiteDatabase baseDeDatos = adminBD.getWritableDatabase();
        
        ListaPuntos = new ArrayList<>();
        String[] columns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_ADDRESS, DatabaseHelper.COLUMN_LATITUDE, DatabaseHelper.COLUMN_LONGITUDE /* , y otras columnas que necesites */};
        Cursor fila = baseDeDatos.query(DatabaseHelper.TABLE_RECYCLING_POINTS, columns, null, null, null, null, null);
        
        //Verificamos que al menos se tenga un elemento en la base de datos
        if (fila.moveToFirst()) {
            do {
                // Asumiendo que has seleccionado todas las columnas necesarias en tu query
                String nombre = fila.getString(fila.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String direccion = fila.getString(fila.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                double latitude = fila.getDouble(fila.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE)); // Necesitarás seleccionar esta columna
                double longitude = fila.getDouble(fila.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE)); // Necesitarás seleccionar esta columna
              
                int id = fila.getInt(fila.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                ListaPuntos.add(new RecyclingPoint(id, nombre, direccion, latitude, longitude));
                
            } while (fila.moveToNext());
        } else {
            Toast.makeText(this, "No hay puntos registrados", Toast.LENGTH_SHORT).show();
        }
        
        
        fila.close();
        baseDeDatos.close();
        
        // Mostrar la lista que trajo nuestro cursor de la BD
        ArrayAdapter<RecyclingPoint> AdapterString = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, ListaPuntos);
        lv_lista.setAdapter(AdapterString);
        
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