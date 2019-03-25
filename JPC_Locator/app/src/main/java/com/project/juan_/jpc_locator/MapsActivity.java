package com.project.juan_.jpc_locator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.project.juan_.jpc_locator.Entidades.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager mLocationManager;
    final Usuario usuario = new Usuario();

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
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

        // Con esto le damos al usuario el poder controlar el zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        subirLatLongFirebase();


    }

    private void obtenerLatLong() {
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

                    // Creamos un markerOptions que es donde vamos a poner los puntos en el mapa
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(Latitud,Longitud));
                    markerOptions.title(snapshot.getValue(Usuario.class).getNombre());

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
                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("posición").setValue(coord);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),17));
                            obtenerLatLong();
                        }
                    }
                });

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    Map<String,Object> coord = new HashMap<>();
                    coord.put("Latitud",location.getLatitude());
                    coord.put("Longitud",location.getLongitude());
                    mDatabase.child("Usuarios").child(usuario.getUsuario()).child("posición").setValue(coord);
                    obtenerLatLong();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
