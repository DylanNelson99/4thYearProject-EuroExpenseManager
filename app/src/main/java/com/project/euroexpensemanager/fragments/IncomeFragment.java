package com.project.euroexpensemanager.fragments;
//Getting data for Income page (Income Only)
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.euroexpensemanager.R;
import com.project.euroexpensemanager.adapters.RecentTransactionAdapter;
import com.project.euroexpensemanager.databasecontroller.DatabaseAddresses;
import com.project.euroexpensemanager.databinding.FragmentIncomeBinding;
import com.project.euroexpensemanager.datamodel.NewEntryModelClass;
import com.project.euroexpensemanager.listeners.OnGetEntriesListener;
import com.project.euroexpensemanager.repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class IncomeFragment extends Fragment implements RecentTransactionAdapter.DeleteSubItemInterface {
    private FragmentIncomeBinding mBinding;
    private RecentTransactionAdapter adapter;
    private KProgressHUD progressHUD;
    private static final String TAG = "IncomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_income, container, false);
        progressHUD = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mBinding.incomeRecyclerViewId.setLayoutManager(layoutManager);
        mBinding.incomeRecyclerViewId.setHasFixedSize(true);
        getNewEntriesFromFireStore();
        return mBinding.getRoot();
    }

    private void getNewEntriesFromFireStore() {
        progressHUD.show();
        Log.i(TAG, "getNewEntriesFromFireStore: currentUserId" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        Repository.getIncome(FirebaseAuth.getInstance().getCurrentUser().getUid(), new OnGetEntriesListener() {
            @Override
            public void onLoaded(List<NewEntryModelClass> listNewEntry) {
                progressHUD.dismiss();
                Log.i(TAG, "onLoaded: listSize" + listNewEntry.size());
                adapter = new RecentTransactionAdapter(listNewEntry, getContext(), IncomeFragment.this);
                mBinding.incomeRecyclerViewId.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String e) {
                progressHUD.dismiss();
                Log.i(TAG, "onFailure: Error" + e);
            }

            @Override
            public void onEmpty() {
                progressHUD.dismiss();
                Toast.makeText(getContext(), "No Recent Transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getDeleteInterface(NewEntryModelClass newEntryModelClass) {
        final PrettyDialog prettyDialog = new PrettyDialog(getContext());
        prettyDialog.setCancelable(false);
        prettyDialog
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure you want to delete your data!")
                .setIcon(R.drawable.pdlg_icon_info)
                .setIconTint(R.color.app_color)
                .addButton("Ok",
                        R.color.pdlg_color_white,
                        R.color.app_color,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                prettyDialog.dismiss();
                                DatabaseAddresses.getNewEntryCollection(newEntryModelClass.getUserId(), newEntryModelClass.getDocId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(TAG, "onFailure: Error" + e.getMessage());
                                    }
                                });
                            }
                        }).addButton("Cancel",
                R.color.pdlg_color_white,
                R.color.app_color,
                new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        prettyDialog.dismiss();
                    }
                }).show();
    }
}