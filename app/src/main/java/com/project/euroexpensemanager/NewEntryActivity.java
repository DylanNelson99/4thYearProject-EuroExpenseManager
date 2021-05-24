package com.project.euroexpensemanager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.project.euroexpensemanager.databasecontroller.DatabaseAddresses;
import com.project.euroexpensemanager.databasecontroller.DatabaseUploader;
import com.project.euroexpensemanager.databinding.ActivityNewEntryBinding;
import com.project.euroexpensemanager.datamodel.NewEntryModelClass;
import com.project.euroexpensemanager.firestoragecontroller.FireStorageAddresses;
import com.project.euroexpensemanager.firestoragecontroller.FireStorageUploader;
import com.project.euroexpensemanager.listeners.OnFileUploadListener;
import com.project.euroexpensemanager.listeners.OnSetUserRecordTaskListeners;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Creating new Entry
public class NewEntryActivity extends AppCompatActivity {
    private ActivityNewEntryBinding mBinding;
    private DatePickerDialog picker;
    private String stDate, stTime, stTypeName, stCategory, stTitle, stDescription, stAmount, stMonthOfYear;
    private static final String TAG = "NewEntryActivity";
    private Uri pickedUri;
    private KProgressHUD progressHUD;
    private int monthOfYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_entry);

        progressHUD = KProgressHUD.create(this)
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
		//calendar method
        mBinding.etDateId.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            picker = new DatePickerDialog(NewEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                    int day = datePicker.getDayOfMonth();
                    monthOfYear = datePicker.getMonth();
                    int year = datePicker.getYear();
                    calendar.set(year, monthOfYear, day);


                    SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM", Locale.ENGLISH);

                    stMonthOfYear = sdfMonth.format(calendar.getTime());

                    Log.i(TAG, "onDateSet: stMonthOfYear" + stMonthOfYear);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    stDate = sdf.format(calendar.getTime());


                    mBinding.etDateId.setText(stDate);
                }
            }, year, month, day);
            picker.show();
        });
		//Time method
        mBinding.etTimeId.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(NewEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String AM_PM;
                    if (selectedHour < 12) {
                        AM_PM = "AM";
                    } else {
                        AM_PM = "PM";
                    }

                    stTime = selectedHour + ":" + selectedMinute + ":" + AM_PM;
                    Log.i(TAG, "onTimeSet: stTime" + stTime);

                    mBinding.etTimeId.setText(selectedHour + ":" + selectedMinute + ":" + AM_PM);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });
        mBinding.getImageId.setOnClickListener(view -> {
            choseImageMethodDialogue();
        });

        mBinding.selectTypeSpinnerValueId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                stTypeName = adapterView.getItemAtPosition(position).toString();
                Log.i(TAG, "onItemSelected: stTypeName" + stTypeName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBinding.selectCategorySpinnerValueId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stCategory = adapterView.getItemAtPosition(i).toString();
                Log.i(TAG, "onItemSelected: stCategoryName" + stCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBinding.saveEntryButtonId.setOnClickListener(view -> {
            if (isValid()) {
                String docId = DatabaseAddresses.getDocId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.i(TAG, "setListeners: docId" + docId);
                uploadNewEntryPhotoOnStorage(pickedUri, stTypeName, stTitle, stDescription, stDate, stTime, stAmount, stCategory, FirebaseAuth.getInstance().getCurrentUser().getUid(), docId, stMonthOfYear);
            }
        });
    }

    // checkPermission external storage
    private void choseImageMethodDialogue() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewEntryActivity.this);
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        } else {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, 1);
                        }
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    openFileChooser();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // get image from Gallery
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pickedUri = data.getData();


            Glide
                    .with(NewEntryActivity.this)
                    .load(pickedUri)
                    .centerCrop()
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.no_image)
                    .into(mBinding.ivProfileId);

            mBinding.ivPhotoId.setVisibility(View.GONE);

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                mBinding.ivProfileId.setImageBitmap(imageBitmap);
                pickedUri = getImageUri(getApplicationContext(), imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // convert bitmap to uri
    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    // take a picture from camera
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

		//Making sure mandatory fields are filled
    private boolean isValid() {
        stTitle = mBinding.etTitleId.getText().toString().trim();
        stDescription = mBinding.etDescriptionId.getText().toString().trim();
        stDate = mBinding.etDateId.getText().toString().trim();
        stTime = mBinding.etTimeId.getText().toString().trim();
        stAmount = mBinding.etAmountId.getText().toString().trim();


        boolean result = true;
        if (pickedUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            result = false;
        } else if (stTime.equals("") || stTime.isEmpty()) {
            mBinding.etTitleId.setError("Please enter title");
            result = false;
        } else if (stDescription.equals("") || stDescription.isEmpty()) {
            mBinding.etDescriptionId.setError("Please enter description");
            result = false;
        } else if (stDate.equals("") || stDate.isEmpty()) {
            mBinding.etDateId.setError("Please select date");
            result = false;
        } else if (stTime.equals("") || stTime.isEmpty()) {
            mBinding.etTimeId.setError("Please select time");
            result = false;
        } else if (stAmount.equals("") || stAmount.isEmpty()) {
            mBinding.etAmountId.setError("Please enter amount");
            result = false;
        }
        return result;
    }

    // upload data on storage
    private void uploadNewEntryPhotoOnStorage(Uri imageUri, String type, String title, String description, String date, String time, String amount, String category, String userId, String docId, String stMonthOfYear) {
        progressHUD.show();

        FireStorageUploader.uploadFile(FireStorageAddresses.getUserProfileStorage(), imageUri, new OnFileUploadListener() {
            @Override
            public void onFileUploaded(String url) {
                Log.i(TAG, "onFileUploaded: url" + url);
                uploadDataOnFireStore(url, type, title, description, date, time, amount, category, userId, docId, stMonthOfYear);
            }

            @Override
            public void onProgress(UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressHUD.setProgress((int) progress);
            }

            @Override
            public void onFailure(String e) {
                progressHUD.dismiss();
                Log.i(TAG, "onFailure: Error" + e);
            }
        });

    }


    private void uploadDataOnFireStore(String imageUri, String type, String title, String description, String date, String time, String amount, String category, String userId, String docId, String stMonthOfYear) {
        Date dateTimeStamp = new Date();
        final Timestamp currentTimeStamp = new Timestamp(dateTimeStamp);
        NewEntryModelClass newEntryModelClass = new NewEntryModelClass(imageUri, type, title, description, date, time, amount, category, userId, docId, currentTimeStamp, stMonthOfYear);
        DatabaseUploader.setNewEntryRecord(newEntryModelClass, new OnSetUserRecordTaskListeners() {
            @Override
            public void onTaskSuccess() {
                progressHUD.dismiss();
                Toast.makeText(NewEntryActivity.this, "New Entry added", Toast.LENGTH_SHORT).show();
                mBinding.etTitleId.setText("");
                mBinding.etDescriptionId.setText("");
                mBinding.etDateId.setText("");
                mBinding.etTimeId.setText("");
                mBinding.etAmountId.setText("");
                finish();
            }

            @Override
            public void onTaskFailed(String e) {
                progressHUD.dismiss();
                Log.i(TAG, "onTaskFailed: Error" + e);
            }
        });

    }


}