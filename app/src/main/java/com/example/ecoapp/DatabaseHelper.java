package com.example.ecoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

// Importa la clase RecyclingPoint para que el código la reconozca
import com.example.ecoapp.RecyclingPoint;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ecoapp.db";
    private static final int DATABASE_VERSION = 1;

    // Nombres de la tabla y columnas
    public static final String TABLE_RECYCLING_POINTS = "recycling_points";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Sentencia SQL para crear la tabla
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_RECYCLING_POINTS + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
            + " TEXT NOT NULL, " + COLUMN_ADDRESS
            + " TEXT NOT NULL, " + COLUMN_LATITUDE
            + " REAL NOT NULL, " + COLUMN_LONGITUDE
            + " REAL NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        // Insertar los datos iniciales
        insertInitialData(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECYCLING_POINTS);
        onCreate(db);
    }

    // Método para insertar los 5 puntos de reciclaje con sus direcciones y coordenadas
    private void insertInitialData(SQLiteDatabase db) {
        insertRecyclingPoint(db, "Supermercado El Proveedor", "Cra. 4 #9-38, Anserma, Caldas", 5.230944, -75.787041);
        insertRecyclingPoint(db, "Parque Mariscal Jorge Robledo", "# a 7-64,, Cra. 5 #72, Anserma, Caldas", 5.229501, -75.787781);
        insertRecyclingPoint(db, "Tiendas Ara", "Carrera 4 # 10 - 30, Caldas, Anserma Viejo", 5.231485, -75.786864);
        insertRecyclingPoint(db, "Supermercado La Remesa Pradera", "a 25-78, Cra. 3 #25-2, Anserma, Caldas", 5.243346, -75.781048);
        insertRecyclingPoint(db, "CREA Anserma", "Cra. 4 #15-21, Anserma, Caldas", 5.234646, -75.785943);
    }

    public void insertRecyclingPoint(SQLiteDatabase db, String name, String address, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        db.insert(TABLE_RECYCLING_POINTS, null, values);
    }

    // Método para obtener todos los puntos de reciclaje
    public List<RecyclingPoint> getAllRecyclingPoints() {
        List<RecyclingPoint> points = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECYCLING_POINTS,
                new String[] {COLUMN_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_LATITUDE, COLUMN_LONGITUDE},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                RecyclingPoint point = new RecyclingPoint(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                );
                points.add(point);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return points;
    }
    
    public RecyclingPoint getRecyclingPointByName(SQLiteDatabase db, String nombre) {
        RecyclingPoint punto = null;
        
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECYCLING_POINTS + " WHERE " + COLUMN_NAME + " = ?",
                new String[]{nombre}
        );
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nombre_punto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String direccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
            double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
            
            // Ahora pasamos TODOS los datos correctos
            punto = new RecyclingPoint(id, nombre_punto, direccion, latitud, longitud);
        }
        
        cursor.close();
        return punto;
    }
}