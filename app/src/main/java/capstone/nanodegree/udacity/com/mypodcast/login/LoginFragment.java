package capstone.nanodegree.udacity.com.mypodcast.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment;
import capstone.nanodegree.udacity.com.mypodcast.model.User;

/**
 * Represents Sign in screen and functionality of the app
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = LoginFragment.class.getSimpleName();
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    @BindView(R.id.edit_text_email)
    EditText mEditTextEmailInput;
    @BindView(R.id.edit_text_password)
    EditText mEditTextPasswordInput;
    @BindView(R.id.linear_layout_login_activity)
    LinearLayout linearLayoutLoginActivity;
    FirebaseAuth mAuth;
    @BindView(R.id.login_with_password)
    Button btnLogin;
    String mUserEmail, mPassword;
    @BindView(R.id.login_with_google)
    SignInButton btnGoogleSign;

    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;
    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;
    GoogleApiClient mGoogleApiClient;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_login, container, false);
        unbinder = ButterKnife.bind(this,view);
        mAuth = FirebaseAuth.getInstance();
        mAuthProgressDialog = new ProgressDialog(getActivity());
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(), this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        return view;
    }




    /**
     * Open CreateAccountFragment when user taps on "Sign up" TextView
     */
    @OnClick(R.id.tv_sign_up)
    public void onSignUpPressed() {
        CreateAccountFragment createAccountFragment = new CreateAccountFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_container, createAccountFragment).commit();
    }


    /**
     * Sign in with Password provider (used when user taps "Done" action on keyboard)
     */
    @OnClick(R.id.login_with_password)
    public void signInPassword() {

        mUserEmail = mEditTextEmailInput.getText().toString();
        mPassword = mEditTextPasswordInput.getText().toString();
        if (mUserEmail.equals("")) {
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (mPassword.equals("")) {
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        mAuthProgressDialog.show();


        mAuth.signInWithEmailAndPassword(mUserEmail, mPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("success:", task.toString());
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        MainFragment mainFragment = new MainFragment();
                        getFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor spe = sp.edit();
                        spe.putString("email", user.getEmail().toLowerCase());
                        spe.apply();
                        //Log.d(LOG_TAG, "providerdatainfo:" + user.getProviderId() + "|" + user.getProviderData().get(0).toString());
                    } else {
                        Toast.makeText(getContext(),
                                "Email not vérified. Please create account and verify the email" + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("failed:", task.getException() + "");
                }
                mAuthProgressDialog.dismiss();
            }
        });
    }


    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }


    /* Sets up the Google Sign In Button : https://developers.google.com/android/reference/com/google/android/gms/common/SignInButton */
    @OnClick(R.id.login_with_google)
    void setupGoogleSignIn() {
        btnGoogleSign.setSize(SignInButton.SIZE_WIDE);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();

    }


    /**
     * This callback is triggered when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                mGoogleAccount = result.getSignInAccount();
                mAuthProgressDialog.show();

                AuthCredential credential = GoogleAuthProvider.getCredential(mGoogleAccount.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(LOG_TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                        SharedPreferences.Editor spe = sp.edit();
                                        if (mGoogleApiClient.isConnected()) {
                                            spe.putString("email", user.getEmail().toLowerCase());
                                            spe.apply();

                                        }
                                        String mEncodedEmail = user.getEmail().replace(".", ",");
                                        String userName = user.getDisplayName();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mEncodedEmail);
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null) {
                                                    HashMap<String, Object> timestampJoined = new HashMap<>();
                                                    timestampJoined.put("timestamp", ServerValue.TIMESTAMP);

                                                    User newUser = new User(userName, mEncodedEmail, timestampJoined);
                                                    ref.setValue(newUser);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        MainFragment mainFragment = new MainFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();

                                    }
                                    mAuthProgressDialog.dismiss();

                                } else {
                                    mAuthProgressDialog.dismiss();
                                    Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());

                                }
                            }
                        });
            } else {
                mAuthProgressDialog.dismiss();
            }
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mAuthProgressDialog.dismiss();

        showErrorToast(connectionResult.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    /*@Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }*/
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}