package com.project.euroexpensemanager.databasecontroller;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DatabaseAddresses {
    private static final String TAG = "DatabaseAddresses";


    // userAccountCollection
    public static DocumentReference getUserAccountCollection(String docId) {
        return FirebaseFirestore.getInstance().collection("UserAccount")
                .document(docId);
    }

    // userProfile Collection
    public static CollectionReference getUserProfiles() {
        return FirebaseFirestore.getInstance().collection("UserAccount");
    }

    // new Entry Collection
    public static DocumentReference getNewEntryCollection(String colId, String docId) {
        return FirebaseFirestore.getInstance().collection("NewEntry")
                .document(colId).collection("Entries").document(docId);
    }

    public static String getDocId(String userId) {
        return FirebaseFirestore.getInstance().collection("NewEntry")
                .document(userId).collection("Entries").document().getId();
    }

    // userNewEntryCollection
    public static DocumentReference getNewEntryCollection(String docId) {
        return FirebaseFirestore.getInstance().collection("NewEntry")
                .document(docId);
    }

    // getEntries
    public static Query getAllEntriesCollection(String userId) {
        return FirebaseFirestore.getInstance().collection("NewEntry")
                .document(userId).collection("Entries").orderBy("currentTimeStamp", Query.Direction.ASCENDING);
    }

    // getEntries
    public static Query getAllEntriesCollectionForIncome(String userId) {
        return FirebaseFirestore.getInstance().collection("NewEntry")
                .document(userId).collection("Entries").whereEqualTo("type", "Income");
    }

    // getEntries
    public static Query getAllEntriesCollectionForExpense(String userId) {
        return FirebaseFirestore.getInstance().collection("NewEntry")
                .document(userId).collection("Entries").whereEqualTo("type", "Expense");
    }
}
