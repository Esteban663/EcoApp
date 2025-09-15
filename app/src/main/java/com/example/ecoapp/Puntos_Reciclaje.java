package com.example.ecoapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;

public class Puntos_Reciclaje extends AppCompatActivity
    {
        private TextInputEditText et_nombre_sitio, et_direccion;
        private MaterialButton btn_agregar, btn_buscar, btn_eliminar;
        private Button btn_atras;
        
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_puntos_reciclaje);
                
                et_nombre_sitio = findViewById(R.id.et_nombre_sitio);
                et_direccion = findViewById(R.id.et_direccion);
                btn_agregar = findViewById(R.id.btn_agregar);
                btn_buscar = findViewById(R.id.btn_buscar);
                btn_eliminar = findViewById(R.id.btn_eliminar);
                btn_atras = findViewById(R.id.btn_atras);
                
                OnClickButtonsListener();
                
                
            }
        
        public void AgregarPunto()
            {
                String nombre = et_nombre_sitio.getText().toString().trim();
                String direccion = et_direccion.getText().toString().trim();
                
                // Verificar que los campos no estén vacíos
                if (nombre.isEmpty() || direccion.isEmpty())
                    {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                
                try
                    {
                        List<Address> direcciones = geocoder.getFromLocationName(direccion, 1);
                        if (direcciones != null && !direcciones.isEmpty())
                            {
                                double latitud = direcciones.get(0).getLatitude();
                                double longitud = direcciones.get(0).getLongitude();
                                
                                //Insertar en la base de datos
                                DatabaseHelper dbHelper = new DatabaseHelper(this);
                                dbHelper.insertRecyclingPoint(dbHelper.getWritableDatabase(), nombre, direccion, latitud, longitud);
                                
                                
                                Toast.makeText(this, "Punto de reciclaje agregado", Toast.LENGTH_SHORT).show();
                                
                                // Limpiar los campos
                                et_nombre_sitio.setText("");
                                et_direccion.setText("");
                                
                            } else
                            {
                                Toast.makeText(this, "Dirección no válida", Toast.LENGTH_SHORT).show();
                            }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al obtener las coordenadas", Toast.LENGTH_SHORT).show();
                    }
            }
        
        public void Buscar_Punto() {
            String nombre = et_nombre_sitio.getText().toString().trim();
            
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingrese el nombre del sitio a buscar", Toast.LENGTH_SHORT).show();
                return;
            }
            
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            
            // Llamamos al método para buscar por nombre (lo tendrás que implementar en tu DatabaseHelper)
            RecyclingPoint punto = dbHelper.getRecyclingPointByName(dbHelper.getReadableDatabase(), nombre);
            
            if (punto != null) {
                // Mostramos los datos en los EditText
                et_nombre_sitio.setText(punto.getName());
                et_direccion.setText(punto.getAddress());
                
                Toast.makeText(this, "Punto encontrado: " + punto.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontró el punto de reciclaje", Toast.LENGTH_SHORT).show();
            }
        }
        
        public void Eliminar(){
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            String nombre = et_nombre_sitio.getText().toString().trim();
            
            if(nombre.isEmpty()){
                Toast.makeText(this, "Ingrese el nombre del sitio a eliminar", Toast.LENGTH_SHORT).show();
            }
            
            int cantidad = db.delete("recycling_points", "name = ?", new String[]{nombre});
            db.close();
            
            et_nombre_sitio.setText("");
            et_direccion.setText("");
            
            if(cantidad == 1){
                Toast.makeText(this, "Punto de reciclaje eliminado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontró el punto de reciclaje", Toast.LENGTH_SHORT).show();
            }
        }
        
        
        public void OnClickButtonsListener()
            {
                btn_agregar.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                AgregarPunto();
                            }
                    });
                
                btn_buscar.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                             Buscar_Punto();
                            }
                    });
                btn_eliminar.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                             Eliminar();
                            }
                    });
                btn_atras.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                Intent intent = new Intent(Puntos_Reciclaje.this, Mapa.class);
                                startActivity(intent);
                                
                            }
                    });
            }
        
    }
    