package com.example.ecoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ConsejosActivity extends AppCompatActivity {
    private LinearLayout consejosLayout;
    private Button btnAtras;
    private FirebaseFirestore db;
    private static final String TAG = "ConsejosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consejos);

        // Inicializar vistas
        consejosLayout = findViewById(R.id.consejosLayout);
        btnAtras = findViewById(R.id.btn_atras);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        // Configurar botón atrás
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsejosActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Cargar consejos desde Firebase
        cargarConsejos();
    }

    private void cargarConsejos() {
        // Mostrar mensaje de carga
        TextView cargando = new TextView(this);
        cargando.setText("Cargando consejos...");
        cargando.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cargando.setPadding(16, 32, 16, 32);
        consejosLayout.addView(cargando);

        db.collection("recycling_tips")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Limpiar el mensaje de carga
                    consejosLayout.removeAllViews();

                    Log.d(TAG, "Documentos encontrados: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        // Si no hay consejos, mostrar mensaje
                        TextView sinConsejos = new TextView(this);
                        sinConsejos.setText("No hay consejos disponibles");
                        sinConsejos.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        sinConsejos.setPadding(16, 32, 16, 32);
                        consejosLayout.addView(sinConsejos);
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Consejo consejo = document.toObject(Consejo.class);
                            Log.d(TAG, "Procesando consejo: " + consejo.getTitulo());

                            // Inflar el layout de la tarjeta
                            View cardView = LayoutInflater.from(this)
                                    .inflate(R.layout.item_consejo, consejosLayout, false);

                            // Obtener referencias a los TextViews
                            TextView tvTitulo = cardView.findViewById(R.id.tvTitulo);
                            TextView tvDescripcion = cardView.findViewById(R.id.tvDescripcion);
                            TextView tvCategoria = cardView.findViewById(R.id.tvCategoria);
                            MaterialCardView materialCard = (MaterialCardView) cardView;

                            // Asignar datos
                            tvTitulo.setText(consejo.getTitulo() != null ? consejo.getTitulo() : "Sin título");
                            tvDescripcion.setText(consejo.getDescripcion() != null ? consejo.getDescripcion() : "Sin descripción");
                            tvCategoria.setText(consejo.getCategoria() != null ? consejo.getCategoria() : "General");

                            // Aplicar color de fondo
                            try {
                                if (consejo.getColor() != null && !consejo.getColor().isEmpty()) {
                                    int color = Color.parseColor(consejo.getColor());
                                    materialCard.setCardBackgroundColor(color);
                                } else {
                                    materialCard.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Verde por defecto
                                }
                            } catch (IllegalArgumentException e) {
                                Log.w(TAG, "Color inválido: " + consejo.getColor());
                                materialCard.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Verde por defecto
                            }

                            // Agregar la tarjeta al layout
                            consejosLayout.addView(cardView);

                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando documento: " + document.getId(), e);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Limpiar el mensaje de carga
                    consejosLayout.removeAllViews();

                    Log.e(TAG, "Error cargando consejos", e);
                    Toast.makeText(this, "Error al cargar consejos: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Mostrar mensaje de error
                    TextView error = new TextView(this);
                    error.setText("Error al cargar consejos\nVerifique su conexión a internet");
                    error.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    error.setPadding(16, 32, 16, 32);
                    consejosLayout.addView(error);
                });
    }
}