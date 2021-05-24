package com.project.euroexpensemanager.beforelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.project.euroexpensemanager.R;
import com.project.euroexpensemanager.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Forgot password method
public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding mBinding;
    private String stEmail;
    private static final String TAG = "ForgotPasswordActivity";
    private KProgressHUD progressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        progressHUD = KProgressHUD.create(ForgotPasswordActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        setListeners();
    }

    private void setListeners() {
        mBinding.ivBackArrowId.setOnClickListener(view -> {
            super.onBackPressed();
        });
        mBinding.btnSendEmailId.setOnClickListener(view -> {
            if (isValid()) {
                progressHUD.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(stEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressHUD.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            Toast.makeText(ForgotPasswordActivity.this, "Successfully send your response on your email", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Error" + e.getLocalizedMessage());
                    }
                });
            }
        });
    }

    // validations to check if email exsits in our db
    private boolean isValid() {
        stEmail = mBinding.etUserEmailIdForgotPassword.getText().toString().trim();

        boolean result = true;

        if (stEmail.equals("") || stEmail.isEmpty()) {
            mBinding.etUserEmailIdForgotPassword.setError("Please enter email address");
            result = false;
        } else if (!isEmailValid(stEmail)) {
            mBinding.etUserEmailIdForgotPassword.setError("Please enter valid email");
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
}