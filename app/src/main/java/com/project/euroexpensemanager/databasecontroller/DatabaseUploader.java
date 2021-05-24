package com.project.euroexpensemanager.databasecontroller;

import androidx.annotation.NonNull;

import com.project.euroexpensemanager.datamodel.NewEntryModelClass;
import com.project.euroexpensemanager.datamodel.SignUpDataModel;
import com.project.euroexpensemanager.listeners.OnSetUserRecordTaskListeners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class DatabaseUploader {
    private static final String TAG = "DatabaseUploader";

    public static void setUserAccountRecord(SignUpDataModel signUpDataModel, OnSetUserRecordTaskListeners onSetUserRecordTaskListeners) {
        DatabaseAddresses.getUserAccountCollection(signUpDataModel.getId())
                .set(signUpDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    onSetUserRecordTaskListeners.onTaskSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSetUserRecordTaskListeners.onTaskFailed(e.getMessage());
            }
        });
    }

    public static void setNewEntryRecord(NewEntryModelClass newEntryRecord, OnSetUserRecordTaskListeners onSetUserRecordTaskListeners) {

        Map<String, Object> data = new HashMap<>();
        data.put("temp", "temp");

        DatabaseAddresses.getNewEntryCollection(newEntryRecord.getUserId())
                .set(data);

        DatabaseAddresses.getNewEntryCollection(newEntryRecord.getUserId(), newEntryRecord.getDocId()).set(newEntryRecord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        onSetUserRecordTaskListeners.onTaskSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSetUserRecordTaskListeners.onTaskFailed(e.getMessage());
            }
        });
    }
}
