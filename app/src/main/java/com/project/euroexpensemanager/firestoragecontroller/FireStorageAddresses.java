package com.project.euroexpensemanager.firestoragecontroller;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FireStorageAddresses {
    public static StorageReference getUserProfileStorage() {
        return FirebaseStorage.getInstance().getReference("NewEntry").child("Images");
    }
}
