package com.example.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.agenda.models.Evento;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText etNombre;
    private Spinner spAsunto;
    private CalendarView mCalendarView;
    private long fechaMili;
    private ListView lvEventos;
    private ArrayList<Evento> listaEventos = new ArrayList<>();
    private ArrayAdapter<Evento> eventoArrayAdapter;
    private Evento eventoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = (EditText) findViewById(R.id.editTextNombre);
        spAsunto = (Spinner) findViewById(R.id.spinnerAsunto);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        lvEventos = (ListView) findViewById(R.id.lvEventos);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(i, i1, i2);
                fechaMili = c.getTimeInMillis();
            }
        });
        lvEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 eventoSeleccionado = (Evento) adapterView.getItemAtPosition(i);
                 etNombre.setText(eventoSeleccionado.getNombre());
                 ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) spAsunto.getAdapter();
                 spAsunto.setSelection(array_spinner.getPosition(eventoSeleccionado.getAsunto()));
                 mCalendarView.setDate(eventoSeleccionado.getFecha());
             }
        });


        inicializarFirebase();
        listarEventos();
    }

    private void listarEventos(){
        databaseReference.child("Evento").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaEventos.clear();
                for(DataSnapshot obj: snapshot.getChildren()) {
                    Evento e = obj.getValue(Evento.class);
                    listaEventos.add(e);
                }
                eventoArrayAdapter = new ArrayAdapter<Evento>(MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        listaEventos);
                lvEventos.setAdapter(eventoArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String nombre = etNombre.getText().toString();
        String asunto = spAsunto.getSelectedItem().toString();
        switch (item.getItemId()){
            case R.id.icon_add: {
                    if (validacion()) {
                        Evento e = new Evento();
                        e.setUid(UUID.randomUUID().toString());
                        e.setNombre(nombre);
                        e.setAsunto(asunto);
                        e.setFecha(fechaMili);

                        databaseReference.child("Evento").child(e.getUid()).setValue(e);
                        limpiar();
                        Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();

                    }
                break;
                }

            case R.id.icon_delete:
                Evento e = new Evento();
                e.setUid(eventoSeleccionado.getUid());
                databaseReference.child("Evento").child(e.getUid()).removeValue();
                limpiar();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
                break;

            case R.id.icon_save:
                if (validacion()) {
                    Evento e2 = new Evento();
                    e2.setUid(eventoSeleccionado.getUid());
                    e2.setNombre(nombre);
                    e2.setAsunto(asunto);
                    e2.setFecha(fechaMili);

                    databaseReference.child("Evento").child(e2.getUid()).setValue(e2);
                    limpiar();

                    Toast.makeText(this, "Actualizado: " + e2.getNombre(), Toast.LENGTH_SHORT).show();
                    break;
                }
                break;

        }
        return true;
    }
    private boolean validacion(){
        long currentDate = Calendar.getInstance().getTimeInMillis();
        if(etNombre.getText().toString().isEmpty())
        {
            etNombre.setError("Required");
            return false;
        }
        if(fechaMili < currentDate) {
            Toast.makeText(this, "Ingrese una fecha posterior a hoy.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void limpiar(){
        long currentDate = Calendar.getInstance().getTimeInMillis();
        etNombre.setText("");
        spAsunto.setSelection(0);
        mCalendarView.setDate(currentDate);

    }
}