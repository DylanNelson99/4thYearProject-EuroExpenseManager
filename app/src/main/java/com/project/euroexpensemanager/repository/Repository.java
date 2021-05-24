package com.project.euroexpensemanager.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project.euroexpensemanager.databasecontroller.DatabaseAddresses;
import com.project.euroexpensemanager.datamodel.NewEntryModelClass;
import com.project.euroexpensemanager.datamodel.SignUpDataModel;
import com.project.euroexpensemanager.listeners.OnGetEntriesListener;
import com.project.euroexpensemanager.listeners.OnGetUsersEmailListener;
import com.project.euroexpensemanager.listeners.OnUserProfileListeners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Repository {
    private static final String TAG = "Repository";


    // get User Email
    public static void getUsersEmail(OnGetUsersEmailListener onGetUsersEmailListener) {
        DatabaseAddresses.getUserProfiles().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        String email = queryDocumentSnapshot.getString("email");
                        onGetUsersEmailListener.onComplete(email);
                        Log.i(TAG, "onComplete: emailOfUser" + email);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onGetUsersEmailListener.onFailure(e.getMessage());
            }
        });
    }

    public static void getCurrentUserProfile(String uId, OnUserProfileListeners onUserProfileListeners) {
        DatabaseAddresses.getUserAccountCollection(uId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, "onEvent: failed" + e);
                    onUserProfileListeners.onFailed(e.getMessage());
                    return;
                } else {
                    if (snapshot != null && snapshot.exists()) {
                        Log.i(TAG, "onEvent: currentData" + snapshot.getData());
                        SignUpDataModel signUp = snapshot.toObject(SignUpDataModel.class);
                        onUserProfileListeners.onProfileLoaded(signUp);
                    } else {
                        Log.i(TAG, "onEvent: currentDataNull: null");
                    }
                }

            }
        });

    }

    // get all New Entries
    public static void getNewEntries(String userId, OnGetEntriesListener onGetEntriesListener) {
        DatabaseAddresses.getAllEntriesCollection(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i(TAG, "onEvent: error" + error);
                    onGetEntriesListener.onFailure(error.getMessage());
                } else {

                    List<NewEntryModelClass> listNewEntry = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Log.i(TAG, "onEvent: valueSizeNewEvent" + value.size());
                        NewEntryModelClass newEntryModelClass = documentSnapshot.toObject(NewEntryModelClass.class);
                        Log.i(TAG, "onEvent: newEntryModelClass" + newEntryModelClass.getTitle());
                        listNewEntry.add(newEntryModelClass);
                    }

                    if (value.size() > 0) {
                        Log.i(TAG, "onEvent: valueSizeEvent" + value.size() + "\n" + listNewEntry.size());
                        onGetEntriesListener.onLoaded(listNewEntry);
                    } else {
                        Log.i(TAG, "onEvent: onEmpty: Empty");
                        onGetEntriesListener.onEmpty();
                    }
                }
            }
        });
    }

    // get New Entries by month for graph
    public static void getNewEntriesNew(String userId, String month, OnGetEntriesListener onGetEntriesListener) {
        DatabaseAddresses.getAllEntriesCollection(userId).whereEqualTo("monthOfYear", month).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i(TAG, "onEvent: error" + error);
                    onGetEntriesListener.onFailure(error.getMessage());
                } else {
                    Log.i(TAG, "onEvent: ValueData" + value.toString());
                    List<NewEntryModelClass> listNewEntry = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Log.i(TAG, "onEvent: valueSizeNewEvent" + value.size());
                        NewEntryModelClass newEntryModelClass = documentSnapshot.toObject(NewEntryModelClass.class);
                        Log.i(TAG, "onEvent: newEntryModelClass" + newEntryModelClass.getTitle());
                        listNewEntry.add(newEntryModelClass);
                    }

                    if (value.size() > 0) {
                        Log.i(TAG, "onEvent: valueSizeEvent" + value.size() + "\n" + listNewEntry.size());
                        onGetEntriesListener.onLoaded(listNewEntry);
                    } else {
                        Log.i(TAG, "onEvent: onEmpty: Empty");
                        onGetEntriesListener.onEmpty();
                    }
                }
            }
        });

    }

    // get Incomes
    public static void getIncome(String userId, OnGetEntriesListener onGetEntriesListener) {
        DatabaseAddresses.getAllEntriesCollectionForIncome(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i(TAG, "onEvent: error" + error);
                    onGetEntriesListener.onFailure(error.getMessage());
                } else {

                    List<NewEntryModelClass> listNewEntry = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Log.i(TAG, "onEvent: valueSizeNewEvent" + value.size());
                        NewEntryModelClass newEntryModelClass = documentSnapshot.toObject(NewEntryModelClass.class);
                        Log.i(TAG, "onEvent: newEntryModelClass" + newEntryModelClass.getTitle());
                        listNewEntry.add(newEntryModelClass);
                    }

                    if (value.size() > 0) {
                        Log.i(TAG, "onEvent: valueSizeEvent" + value.size() + "\n" + listNewEntry.size());
                        onGetEntriesListener.onLoaded(listNewEntry);
                    } else {
                        Log.i(TAG, "onEvent: onEmpty: Empty");
                        onGetEntriesListener.onEmpty();
                    }
                }
            }
        });
    }

    // get Expenses
    public static void getExpense(String userId, OnGetEntriesListener onGetEntriesListener) {
        DatabaseAddresses.getAllEntriesCollectionForExpense(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i(TAG, "onEvent: error" + error);
                    onGetEntriesListener.onFailure(error.getMessage());
                } else {

                    List<NewEntryModelClass> listNewEntry = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Log.i(TAG, "onEvent: valueSizeNewEvent" + value.size());
                        NewEntryModelClass newEntryModelClass = documentSnapshot.toObject(NewEntryModelClass.class);
                        Log.i(TAG, "onEvent: newEntryModelClass" + newEntryModelClass.getTitle());
                        listNewEntry.add(newEntryModelClass);
                    }

                    if (value.size() > 0) {
                        Log.i(TAG, "onEvent: valueSizeEvent" + value.size() + "\n" + listNewEntry.size());
                        onGetEntriesListener.onLoaded(listNewEntry);
                    } else {
                        Log.i(TAG, "onEvent: onEmpty: Empty");
                        onGetEntriesListener.onEmpty();
                    }
                }
            }
        });
    }


}
