package capstone.nanodegree.udacity.com.mypodcast.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.User;


/**
 * Represents Sign up screen and functionality of the app
 */
public class CreateAccountFragment extends Fragment {
    private static final String LOG_TAG = CreateAccountFragment.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    @BindView(R.id.edit_text_username_create)
    EditText mEditTextUsernameCreate;
    @BindView(R.id.edit_text_email_create)
    EditText mEditTextEmailCreate;
    @BindView(R.id.edit_text_password_create)
    EditText mEditTextPasswordCreate;
    @BindView(R.id.linear_layout_create_account_activity)
    LinearLayout linearLayoutCreateAccountActivity;
    @BindView(R.id.btn_create_account_final)
    Button btnCreateAccount;
    private FirebaseAuth mAuth;
    private String mUserName, mUserEmail, mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_create_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        mAuthProgressDialog = new ProgressDialog(getContext());
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_creating_user_with_firebase));
        mAuthProgressDialog.setCancelable(false);
        return view;
    }





    /**
     * Create new account using Firebase email/password provider
     */
    @OnClick(R.id.btn_create_account_final)
    public void onCreateAccountPressed() {
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = mEditTextPasswordCreate.getText().toString();
/**
 * Check that email and user name are okay
 */
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validPassword = isPasswordValid(mPassword);
        if (!validEmail || !validUserName || !validPassword) return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        mAuthProgressDialog.show();

        /**
         * Create new user with specified email and password
         */
        mAuth.createUserWithEmailAndPassword(mUserEmail, mPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuthProgressDialog.dismiss();
                    Log.d("success:", task.toString());
                    Log.i(LOG_TAG, getString(R.string.log_message_auth_successful));
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.i(LOG_TAG + ":user", user.getDisplayName() + "|" + user.getEmail());
                    createUserInFirebaseHelper(user);
                } else {
                    mAuthProgressDialog.dismiss();
                    Toast.makeText(getContext(),
                            "Failed:"+task.getException(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    public void createUserInFirebaseHelper(FirebaseUser user) {
        if (user != null) {
            final String encodedEmail = mUserEmail.replace(".", ",");
            String test = encodedEmail.replace(",", ".");
            Log.d("original:", test);
            final DatabaseReference userLocation = FirebaseDatabase.getInstance().getReference("users").child(encodedEmail);
            userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        HashMap<String, Object> timestampJoined = new HashMap<>();
                        timestampJoined.put("timestamp", ServerValue.TIMESTAMP);

                        User newUser = new User(mUserName, encodedEmail, timestampJoined);
                        userLocation.setValue(newUser);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(LOG_TAG, getString(R.string.log_error_occurred) + databaseError.getMessage());
                }
            });
            user.sendEmailVerification()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                LoginFragment login = new LoginFragment();
                                getFragmentManager().beginTransaction().replace(R.id.main_container, login).commit();
                                Toast.makeText(getContext(),
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                Log.d(LOG_TAG, "sendEmailVerification:" + task.isSuccessful());
                            } else {
                                Log.e(LOG_TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(getContext(),
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Log.d(LOG_TAG, "email:" + encodedEmail + "|" + mUserEmail + "|" + user.isEmailVerified());

        }
    }


    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid),
                    email));
            return false;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            mEditTextPasswordCreate.setError(getResources().getString(R.string.error_invalid_password_not_valid));
            return false;
        }
        return true;
    }

}
