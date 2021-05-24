package com.project.euroexpensemanager.listeners;

import com.google.firebase.storage.UploadTask;

public interface OnFileUploadListener {
    public void onFileUploaded(String url);
    public void onProgress(UploadTask.TaskSnapshot snapshot);
    public void onFailure(String e);
}
