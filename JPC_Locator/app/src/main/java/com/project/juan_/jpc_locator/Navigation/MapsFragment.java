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

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MapsFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MapsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

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

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    public MapsFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MapsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static MapsFragment newInstance(String param1, String param2) {
//        MapsFragment fragment = new MapsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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

    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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

                    if(!snapshot.getKey().equals(usuario.getUsuario())){
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
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        });

    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
