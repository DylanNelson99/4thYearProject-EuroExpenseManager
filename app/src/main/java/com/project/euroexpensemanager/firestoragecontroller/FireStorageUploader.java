package com.project.euroexpensemanager.firestoragecontroller;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.project.euroexpensemanager.listeners.OnFileUploadListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FireStorageUploader {
    public static void uploadFile(StorageReference storageReference, Uri uri, OnFileUploadListener onFileUploadListener) {
        storageReference.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        onFileUploadListener.onFileUploaded(String.valueOf(uri));
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                onFileUploadListener.onProgress(snapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFileUploadListener.onFailure(e.getMessage());
            }
        });
    }
}
