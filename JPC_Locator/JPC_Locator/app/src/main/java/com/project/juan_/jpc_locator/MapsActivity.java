package com.project.juan_.jpc_locator;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Creamos una referencia a la base de datos
    private DatabaseReference mDatabase;

    // Creamos un ArrayList de markers para guardarlos. Con estos dos ArrayList vamos a ir actualizando los markers
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        // Con addValueEventListener() lo que va a hacer es que cada vez qeu cambien los valores de coordenadas, se va a lanzar ese método
        // Con CountDownTimer() lo que hace es que revisa los valores cada cierto tiempo. Podemos mirar como funciona en la documentacion
        mDatabase.child("coordenadas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Primero borramos los marcadores para que no se repitan luego en el array realTimeMarker
                for (Marker marker:realTimeMarkers){
                    marker.remove();
                }

                // Con este for recorremos los hijos del nodo coordenadas
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Con esto cogemos los valores que tenemos en la clase MapsData
                    MapsPojo mp = snapshot.getValue(MapsPojo.class);

                    // Cogemos cada uno de los valores
                    Double Latitud = mp.getLatitud();
                    Double Longitud = mp.getLongitud();

                    Log.e("Latitud:", String.valueOf(Latitud));
                    Log.e("Longitud:", String.valueOf(Longitud));

                    // Creamos un markerOptions que es donde vamos a poner los puntos en el mapa
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(Latitud,Longitud));

                    // Usamos los ArrayList para ir actualizando los markers
                    tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
