package br.com.psousa.findeasy.fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.psousa.findeasy.R;

public class ViewPesquisaMaps extends Fragment implements OnMapReadyCallback {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private FragmentManager mFragmentManager;
    private MarkerOptions mMarkerOptions;
    private Marker mMyMarker;
    private LatLng mLocalizacao;
    private Button mBtnProximo;
    private MenuItem mSearchItem;


    private OnViewPesquisaMapsListener mListener;

    public ViewPesquisaMaps() {
        // Required empty public constructor
    }

    public static ViewPesquisaMaps newInstance(double longitude, double latitude){
        ViewPesquisaMaps fragment = new ViewPesquisaMaps();
        Bundle args = new Bundle();
        args.putDouble(LONGITUDE, longitude);
        args.putDouble(LATITUDE, latitude);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapFragment = new SupportMapFragment();

        mMapFragment.getMapAsync(this);

        mFragmentManager = getActivity().getSupportFragmentManager();

        mFragmentManager.beginTransaction().replace(R.id.mapa, mMapFragment).commit();


        if (getArguments() != null) {
            Bundle args = getArguments();
            double longitude = args.getDouble(LONGITUDE);
            double latitude = args.getDouble(LATITUDE);
            mLocalizacao = new LatLng(latitude, longitude);
        } else {
            mLocalizacao = new LatLng(40, -210);
        }

    }

    public void pesquisarLocalizacao(String endereco){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(endereco, 1);
            for (Address address: addresses){
                mLocalizacao = new LatLng(address.getLatitude(), address.getLongitude());
                if (mMarkerOptions == null) {
                    mMarkerOptions = new MarkerOptions()
                            .position(mLocalizacao)
                            .title("Localizacao!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .draggable(true);
                } else {
                    mMyMarker.remove();
                    mMarkerOptions.position(mLocalizacao);
                }

                if ((mMarkerOptions != null) &&
                        (mMap != null)){
                    mMyMarker = mMap.addMarker(mMarkerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalizacao, 17));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_pesquisa_maps, container, false);

        mBtnProximo = (Button) view.findViewById(R.id.btn_pesquisar_ok);

        mBtnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProximo(mMyMarker.getPosition().longitude, mMyMarker.getPosition().latitude);
            }
        });

        return view;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.principal, menu);
        mSearchItem = menu.findItem(R.id.action_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.action_search) {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
            //searchView.setQueryHint(getString(R.string.hint_pesquisa_produto));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    pesquisarLocalizacao(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onProximo(double longitude, double latitude) {
        if (mListener != null) {
            mListener.onProximo(longitude, latitude);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewPesquisaMapsListener) {
            mListener = (OnViewPesquisaMapsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnViewPesquisaMapsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMarkerOptions == null) {
            mMarkerOptions = new MarkerOptions()
                    .position(mLocalizacao)
                    .title("Localizacao!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .draggable(true);
        } else {
            mMyMarker.remove();
            mMarkerOptions.position(mLocalizacao);
        }

        if ((mMarkerOptions != null) &&
                (mMap != null)){
            mMyMarker = mMap.addMarker(mMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalizacao, 17));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnViewPesquisaMapsListener {
        // TODO: Update argument type and name
        void onProximo(double longitude, double latitude);
    }
}
