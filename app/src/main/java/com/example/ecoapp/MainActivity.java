package com.example.ecoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecoapp.R;

public class MainActivity extends AppCompatActivity
    {
        private Button btnMapa, btnConsejos;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                
                btnMapa = findViewById(R.id.btnMapa);
                btnConsejos = findViewById(R.id.btnConsejos);

                OnClickButtonsListener();
     
            }

            public void OnClickButtonsListener(){
                btnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Mapa.class);
                        startActivity(intent);
                    }


                });
                // Agregar este listener para el botón de Consejos
                btnConsejos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ConsejosActivity.class);
                        startActivity(intent);
                    }
                    });
            }
    }