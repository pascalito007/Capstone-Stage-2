package capstone.nanodegree.udacity.com.mypodcast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;


public class AdviewFragment extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.adView)
    AdView mAdView;

    public AdviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_adview, container, false);
        unbinder = ButterKnife.bind(this, view);
//Set up for pre-fetching interstitial ad request
        MobileAds.initialize(getContext(), getString(R.string.add_init_id));


        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.device_id))
                .build();
        mAdView.loadAd(adRequest);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
