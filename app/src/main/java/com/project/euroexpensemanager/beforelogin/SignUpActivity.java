package com.project.euroexpensemanager.beforelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.project.euroexpensemanager.MainActivity;
import com.project.euroexpensemanager.R;
import com.project.euroexpensemanager.databasecontroller.DatabaseUploader;
import com.project.euroexpensemanager.databinding.ActivitySignUpBinding;
import com.project.euroexpensemanager.datamodel.SignUpDataModel;
import com.project.euroexpensemanager.listeners.OnGetUsersEmailListener;
import com.project.euroexpensemanager.listeners.OnSetUserRecordTaskListeners;
import com.project.euroexpensemanager.repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding mBinding;
    private String stEmail, stPassword;
    private KProgressHUD kProgressHUD;
    private static final String TAG = "SignUpActivity";
    List<String> listEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        listEmail = new ArrayList<>();

        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        Repository.getUsersEmail(new OnGetUsersEmailListener() {
            @Override
            public void onComplete(String email) {
                listEmail.add(email);
            }

            @Override
            public void onFailure(String e) {
                Log.i(TAG, "onFailure: Error" + e);
            }
        });

        setListeners();
    }

    private void setListeners() {
        mBinding.ivBackArrowId.setOnClickListener(view -> {
            super.onBackPressed();
        });
		//If an email of same kind already exsists
        mBinding.buttonRegisterId.setOnClickListener(view -> {
            if (isValid()) {
                if (listEmail.contains(stEmail)) {
                    Toast.makeText(this, "Email already exist!", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(stEmail, stPassword);
                }
            }
        });
    }

    private void createUser(String email, String password) {
        kProgressHUD.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: taskResult" + task.getResult().getUser().getUid());
                            uploadDataOnFireStore(task.getResult().getUser().getUid(), email, password);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                kProgressHUD.dismiss();
                Log.i(TAG, "onFailure: error" + e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }

    private void uploadDataOnFireStore(String userId, String email, String password) {
        SignUpDataModel signUpDataModel = new SignUpDataModel(userId, email, password);
        DatabaseUploader.setUserAccountRecord(signUpDataModel, new OnSetUserRecordTaskListeners() {
            @Override
            public void onTaskSuccess() {
                kProgressHUD.dismiss();
                Toast.makeText(SignUpActivity.this, "Account Created Login Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                mBinding.etEmailId.setText("");
                mBinding.etPasswordId.setText("");
            }

            @Override
            public void onTaskFailed(String e) {
                kProgressHUD.dismiss();
                Log.i(TAG, "onTaskFailed: Error" + e);
            }
        });
    }

		//Sign up/sign in validation
    private boolean isValid() {

        stEmail = mBinding.etEmailId.getText().toString().trim();
        stPassword = mBinding.etPasswordId.getText().toString().trim();


        boolean result = true;
        if (stEmail.equals("") || stEmail.isEmpty()) {
            mBinding.etEmailId.setError("Please enter email");
            result = false;
        } else if (!isEmailValid(stEmail)) {
            mBinding.etEmailId.setError("Please enter valid email");
            result = false;
        } else if (stPassword.equals("") || stPassword.isEmpty()) {
            mBinding.etPasswordId.setError("Please enter password");
            result = false;
        } else if (stPassword.length() < 6) {
            mBinding.etPasswordId.setError("Password should be greater then 6 digits"); //Password needs to be longer than 6 digits to be valid
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