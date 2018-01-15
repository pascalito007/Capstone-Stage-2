package capstone.nanodegree.udacity.com.mypodcast.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 15/01/2018.
 */

public class AccountFragment extends Fragment {
    private Unbinder unbinder;
    FirebaseAuth mAuth;
    @BindView(R.id.account_img)
    ImageView imageView;
    @BindView(R.id.account_name)
    TextView name;
    @BindView(R.id.account_email)
    TextView email;
    @BindView(R.id.account_number)
    TextView number;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("firebaseuservalue:", user.getDisplayName() + "|" + user.getEmail() + "|" + user.getPhotoUrl().toString());
        Glide.with(this).load(user.getPhotoUrl().toString()).into(imageView);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        number.setText(user.getPhoneNumber());
        return view;
    }

    @OnClick(R.id.logout)
    public void logout() {
        if (sharedPreferences.getString(Constant.email, null) != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constant.email, null);
            editor.apply();
            mAuth.signOut();
            LoginFragment accountFragment = new LoginFragment();
            getFragmentManager().beginTransaction().replace(R.id.main_container, accountFragment).commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
