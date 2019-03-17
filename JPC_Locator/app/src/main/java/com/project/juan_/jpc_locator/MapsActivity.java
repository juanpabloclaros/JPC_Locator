package com.project.juan_.jpc_locator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationClient;

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
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        subirLatLongFirebase();
        countDownTimer();

    }

    // Con CountDownTimer() lo que hace es que revisa los valores cada cierto tiempo. Podemos mirar como funciona en la documentacion
    private void countDownTimer(){

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                // Aqui vamos a ir actualizando los puntos, vamos a ir cogiendo los valores cada 2 segundos(2000)
                Toast.makeText(MapsActivity.this, "Puntos actualizados", Toast.LENGTH_SHORT).show();
                subirLatLongFirebase();
                onMapReady(mMap);
            }
        }.start();

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
        mDatabase.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Primero borramos los marcadores para que no se repitan luego en el array realTimeMarker
                for (Marker marker:realTimeMarkers){
                    marker.remove();
                }

                // Con este for recorremos los hijos del nodo
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Con esto cogemos los valores que tenemos en la clase MapsData
                    MapsPojo mp = snapshot.child("posición").getValue(MapsPojo.class);

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
                countDownTimer();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void subirLatLongFirebase() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            Con esta linea le pedimos al usuario que active los permisos
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Map<String,Object> coord = new HashMap<>();
                            coord.put("Latitud",location.getLatitude());
                            coord.put("Longitud",location.getLongitude());
                            mDatabase.child("Usuarios").child("651234976").child("posición").setValue(coord);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
