package com.project.euroexpensemanager.beforelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import com.project.euroexpensemanager.MainActivity;
import com.project.euroexpensemanager.R;
import com.project.euroexpensemanager.databinding.ActivityLoginBinding;
import com.project.euroexpensemanager.datamodel.SignUpDataModel;
import com.project.euroexpensemanager.listeners.OnUserProfileListeners;
import com.project.euroexpensemanager.repository.Repository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding mBinding;
    private String stUserEmail, stUserPassword;
    private static final String TAG = "LoginActivity";
    private KProgressHUD progressHUD;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    searchUserAccount(firebaseAuth);
                    Log.d(TAG, "onAuthStateChanged: call");
                }
            }
        };


        setListeners();
    }

    private void setListeners() {
        mBinding.ivShowPasswordId.setOnClickListener(view -> {
            if (mBinding.etPasswordId.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                mBinding.ivShowPasswordId.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                mBinding.etPasswordId.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                mBinding.ivShowPasswordId.setImageResource(R.drawable.ic_baseline_visibility_24);
                mBinding.etPasswordId.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        mBinding.tvSignUpId.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        mBinding.loginButtonId.setOnClickListener(view -> {
            if (isValid()) {
                progressHUD.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(stUserEmail, stUserPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressHUD.dismiss();
                                Log.i(TAG, "onSuccess: AuthResult");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressHUD.dismiss();
                        Log.i(TAG, "onFailure: Error" + e.getLocalizedMessage());
                    }
                });
            }
        });
        mBinding.tvForgotPasswordId.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    // validations
    private boolean isValid() {
        stUserEmail = mBinding.etEmailId.getText().toString().trim();
        stUserPassword = mBinding.etPasswordId.getText().toString().trim();

        boolean result = true;

        if (stUserEmail.equals("") || stUserEmail.isEmpty()) {
            mBinding.etEmailId.setError("Please enter email address");
            result = false;
        } else if (!isEmailValid(stUserEmail)) {
            mBinding.etEmailId.setError("Please enter valid email");
            result = false;
        } else if (stUserPassword.equals("") || stUserPassword.isEmpty()) {
            mBinding.etPasswordId.setError("Please enter password");
            result = false;
        } else if (stUserPassword.length() < 6) {
            Toast.makeText(this, "Password length doesn't match", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    // email validation
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void searchUserAccount(FirebaseAuth firebaseAuth) {
        progressHUD.show();
        Repository.getCurrentUserProfile(firebaseAuth.getCurrentUser().getUid(), new OnUserProfileListeners() {
            @Override
            public void onProfileLoaded(SignUpDataModel signUpDataModel) {
                progressHUD.dismiss();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailed(String e) {
                Log.i(TAG, "onFailed: Error" + e);
            }

            @Override
            public void isEmpty() {
                Toast.makeText(LoginActivity.this, "User not Register", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}