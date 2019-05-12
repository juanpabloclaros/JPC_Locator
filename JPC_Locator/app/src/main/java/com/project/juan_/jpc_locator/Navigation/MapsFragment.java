package com.project.juan_.jpc_locator.Navigation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.project.juan_.jpc_locator.MapsPojo;
import com.project.juan_.jpc_locator.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager mLocationManager;
    final Usuario usuario = new Usuario();
    private String tokenEmisor;

    // Creamos una referencia a la base de datos
    private DatabaseReference mDatabase;

    // Creamos un ArrayList de markers para guardarlos. Con estos dos ArrayList vamos a ir actualizando los markers
    private ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        subirLatLongFirebase();
    }

    private void obtenerLatLong(final Location location) {
        // Con addValueEventListener() lo que va a hacer es que cada vez qeu cambien los valores de coordenadas, se va a lanzar ese método
        mDatabase.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Primero borramos los marcadores para que no se repitan luego en el array realTimeMarker
                for (Marker marker:realTimeMarkers){
                    marker.remove();
                }

                tokenEmisor = dataSnapshot.child(usuario.getUsuario()).getValue(Usuario.class).getToken();
                // Con este for recorremos los hijos del nodo
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    // Con esto cogemos los valores que tenemos en la clase MapsData
                    MapsPojo mp = snapshot.child("posición").getValue(MapsPojo.class);

                    // Cogemos cada uno de los valores
                    Double Latitud = mp.getLatitud();
                    Double Longitud = mp.getLongitud();

                    // Aqui calculamos la distancia entre la posicion actual del usuario con la de los demás
                    float[] results = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            Latitud, Longitud, results);

                    if(!snapshot.getKey().equals(usuario.getUsuario()) && results[0] < 500.0){

                        // Creamos un markerOptions que es donde vamos a poner los puntos en el mapa
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(Latitud,Longitud));
                        markerOptions.title(snapshot.getValue(Usuario.class).getNombre());
                        markerOptions.snippet("Distancia: " + results[0] + " mts");

                        Map<String,Object> valores = new HashMap<>();
                        valores.put("nombre",snapshot.getValue(Usuario.class).getNombre());
                        valores.put("distancia",results[0]);
                        valores.put("tokenEmisor",tokenEmisor);
                        valores.put("tokenReceptor",snapshot.getValue(Usuario.class).getToken());
                        mDatabase.child("Notifications").child("Cerca").child(usuario.getUsuario()).child(snapshot.getKey()).setValue(valores);


                        // Usamos los ArrayList para ir actualizando los markers
                        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));
                    }
                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarkers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void subirLatLongFirebase() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            Con esta linea le pedimos al usuario que active los permisos
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Map<String,Object> coord = new HashMap<>();
                            coord.put("Latitud",location.getLatitude());
                            coord.put("Longitud",location.getLongitude());
                            mDatabase.child("Usuarios").child(usuario.getUsuario()).child("posición").setValue(coord);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),17));
                            obtenerLatLong(location);
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
                    obtenerLatLong(location);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        });

    }
}
