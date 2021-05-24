package com.project.euroexpensemanager.fragments;
//Getting Graph Data
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.project.euroexpensemanager.R;
import com.project.euroexpensemanager.adapters.RecentTransactionAdapter;
import com.project.euroexpensemanager.component.PiaChart;
import com.project.euroexpensemanager.databasecontroller.DatabaseAddresses;
import com.project.euroexpensemanager.databinding.FragmentGraphBinding;
import com.project.euroexpensemanager.datamodel.NewEntryModelClass;
import com.project.euroexpensemanager.listeners.OnGetEntriesListener;
import com.project.euroexpensemanager.repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;


public class GraphFragment extends Fragment {
    private FragmentGraphBinding mBinding;
    private static final String TAG = "GraphFragment";
    private KProgressHUD progressHUD;
    private int sumOfOnline, sumOfDiningOut, sumOfGrocery, sumOfTransport, sumOfBills, sumOfAllValues;
    private int perOfOnline, perOfDiningOut, perOfGrocery, perOfTransport, perOfBills;
    private String monthSelected;
    private String online = "Online", diningOut = "DiningOut", groceries = "Groceries", transport = "Transport", bills = "Bills", stTotalIncome = "Income", stTotalExpense = "Expense";
    private int totalIncome, totalExpense;
    private List<NewEntryModelClass> listNewEntry;
    private String monthOfYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_graph, container, false);
        listNewEntry = new ArrayList<>();

        progressHUD = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);


        mBinding.monthSpinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                monthSelected = mBinding.monthSpinnerId.getSelectedItem().toString();
                Log.i(TAG, "onItemSelected: monthSelected" + monthSelected);
                getDataOfMonthBySnapshot(monthSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return mBinding.getRoot();
    }

    private void getDataOfMonthBySnapshot(String monthDataBySnap) {
        progressHUD.show();
        FirebaseFirestore.getInstance().collection("NewEntry")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Entries")
                .whereEqualTo("monthOfYear", monthDataBySnap)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.i(TAG, "onEvent: errorSnapshotListener" + error.getLocalizedMessage());
                        } else {
                            if (value.size() >= 0) {

                                Log.i(TAG, "onEvent: valueSizeAndValue" + "\n" + value.size() + "\n" + value.toString());
                                progressHUD.dismiss();
                                listNewEntry.clear();
                                sumOfOnline = 0;
                                sumOfDiningOut = 0;
                                sumOfGrocery = 0;
                                sumOfTransport = 0;
                                sumOfBills = 0;
                                sumOfAllValues = 0;
                                perOfOnline = 0;
                                perOfDiningOut = 0;
                                perOfGrocery = 0;
                                perOfTransport = 0;
                                perOfBills = 0;

                                totalIncome = 0;
                                totalExpense = 0;

                                for (QueryDocumentSnapshot documentSnapshots : value) {
                                    String stAmount = documentSnapshots.getString("amount");
                                    String category = documentSnapshots.getString("category");
                                    Timestamp timestamp = documentSnapshots.getTimestamp("currentTimeStamp");
                                    String date = documentSnapshots.getString("date");
                                    String description = documentSnapshots.getString("description");
                                    String docId = documentSnapshots.getString("docId");
                                    String image = documentSnapshots.getString("image");
                                    monthOfYear = documentSnapshots.getString("monthOfYear");
                                    String time = documentSnapshots.getString("time");
                                    String title = documentSnapshots.getString("title");
                                    String type = documentSnapshots.getString("type");
                                    String userId = documentSnapshots.getString("userId");


                                    Log.i(TAG, "onComplete: DataOfEntry" + "\n" + stAmount + "\n" + category + "\n" + monthOfYear + "\n" + type);
                                    NewEntryModelClass newEntryModelClass = new NewEntryModelClass(image, type, title, description, date, time, stAmount, category, userId, docId, timestamp, monthOfYear);
                                    listNewEntry.add(newEntryModelClass);
                                }
                                if (!monthDataBySnap.equals(monthOfYear)) {
                                    Toast.makeText(getContext(), "Graph Data Not Found", Toast.LENGTH_SHORT).show();
                                    mBinding.parentLinearLayoutId.setVisibility(View.GONE);
                                    mBinding.tvNoDataFoundId.setVisibility(View.VISIBLE);
                                } else {
                                    Log.i(TAG, "onComplete: monthDataAndMonthOfYear" + "\n" + monthDataBySnap + "\n" + monthOfYear);
                                    mBinding.parentLinearLayoutId.setVisibility(View.VISIBLE);
                                    mBinding.tvNoDataFoundId.setVisibility(View.GONE);
                                    for (int i = 0; i < listNewEntry.size(); i++) {
                                        Log.i(TAG, "onLoaded: selectedMonthAndDatabaseMonth" + "\n" + monthDataBySnap + "\n" + listNewEntry.get(i).getMonthOfYear());
                                        if (online.equals(listNewEntry.get(i).getCategory())) {
                                            sumOfOnline += Double.parseDouble(listNewEntry.get(i).getAmount());
                                            Log.i(TAG, "onLoaded: onlineCategoryOnline" + sumOfOnline);
                                        }
                                        if (diningOut.equals(listNewEntry.get(i).getCategory())) {
                                            sumOfDiningOut += Double.parseDouble(listNewEntry.get(i).getAmount());
                                            Log.i(TAG, "onLoaded: onlineCategoryDiningOut" + sumOfDiningOut);
                                        }
                                        if (groceries.equals(listNewEntry.get(i).getCategory())) {
                                            sumOfGrocery += Double.parseDouble(listNewEntry.get(i).getAmount());
                                            Log.i(TAG, "onLoaded: onlineCategoryGroceries" + sumOfGrocery);
                                        }
                                        if (transport.equals(listNewEntry.get(i).getCategory())) {
                                            sumOfTransport += Double.parseDouble(listNewEntry.get(i).getAmount());
                                            Log.i(TAG, "onLoaded: onlineCategoryTransport" + sumOfTransport);
                                        }
                                        if (bills.equals(listNewEntry.get(i).getCategory())) {
                                            sumOfBills += Double.parseDouble(listNewEntry.get(i).getAmount());
                                            Log.i(TAG, "onLoaded: onlineCategoryBills" + sumOfBills);
                                        }


                                        if (stTotalIncome.equals(listNewEntry.get(i).getType())) {
                                            totalIncome += Integer.parseInt(listNewEntry.get(i).getAmount());
                                        } else {
                                            totalExpense += Integer.parseInt(listNewEntry.get(i).getAmount());
                                        }

                                        Log.i(TAG, "onLoaded: totalIncomeAndTotalExpense" + "\n" + totalIncome + "\n" + totalExpense);

                                    }


                                    sumOfAllValues = sumOfOnline + sumOfDiningOut + sumOfGrocery + sumOfTransport + sumOfBills;
                                    Log.i(TAG, "onLoaded: sumOfAllValues" + sumOfAllValues);
                                    perOfOnline = (sumOfOnline * 100) / sumOfAllValues;
                                    perOfDiningOut = (sumOfDiningOut * 100) / sumOfAllValues;
                                    perOfGrocery = (sumOfGrocery * 100) / sumOfAllValues;
                                    perOfTransport = (sumOfTransport * 100) / sumOfAllValues;
                                    perOfBills = (sumOfBills * 100) / sumOfAllValues;

//                Log.i(TAG, "onLoaded: percentageOfAllGivenValues" + "\n" + String.format("%.1f", perOfOnline) + "\n" + String.format("%.1f", perOfDiningOut) + "\n" + String.format("%.1f", perOfGrocery) + "\n" + String.format("%.1f", perOfTransport) + "\n" + String.format("%.1f", perOfBills));

                                    mBinding.tvTotalIncomeId.setText(String.valueOf(totalIncome));
                                    mBinding.tvTotalExpenseId.setText(String.valueOf(totalExpense));

                                    mBinding.tvOnlineDataId.setText(String.valueOf(sumOfOnline));
                                    mBinding.tvDiningOutId.setText(String.valueOf(sumOfDiningOut));
                                    mBinding.tvGroceryId.setText(String.valueOf(sumOfGrocery));
                                    mBinding.tvTransportId.setText(String.valueOf(sumOfTransport));
                                    mBinding.tvBillsId.setText(String.valueOf(sumOfBills));

                                    PiaChart.attendancePiaChartGenerator(mBinding.attendanceReportGeneratorPiaChart, perOfOnline, perOfDiningOut, perOfGrocery, perOfTransport, perOfBills);

                                }


                            }
                        }
                    }
                });
    }


//    private void getNewEntriesFromFireStore(String monthSelect) {
//        progressHUD.show();
//        Log.i(TAG, "getNewEntriesFromFireStore: currentUserId" + FirebaseAuth.getInstance().getCurrentUser().getUid());
//        Repository.getNewEntriesNew(FirebaseAuth.getInstance().getCurrentUser().getUid(), monthSelect, new OnGetEntriesListener() {
//            @Override
//            public void onLoaded(List<NewEntryModelClass> listNewEntry) {
//                progressHUD.dismiss();
//                Log.i(TAG, "onLoaded: listGraphFragment:" + listNewEntry.size());
//                sumOfOnline = 0;
//                sumOfDiningOut = 0;
//                sumOfGrocery = 0;
//                sumOfTransport = 0;
//                sumOfBills = 0;
//                sumOfAllValues = 0;
//                perOfOnline = 0;
//                perOfDiningOut = 0;
//                perOfGrocery = 0;
//                perOfTransport = 0;
//                perOfBills = 0;
//
//                totalIncome = 0;
//                totalExpense = 0;
//
//                for (int i = 0; i < listNewEntry.size(); i++) {
//                    if (monthSelect.equals(listNewEntry.get(i).getMonthOfYear())) {
//                        Log.i(TAG, "onLoaded: selectedMonthAndDatabaseMonth" + "\n" + monthSelect + "\n" + listNewEntry.get(i).getMonthOfYear());
//                        if (online.equals(listNewEntry.get(i).getCategory())) {
//                            sumOfOnline += Double.parseDouble(listNewEntry.get(i).getAmount());
//                            Log.i(TAG, "onLoaded: onlineCategoryOnline" + sumOfOnline);
//                            mBinding.tvOnlineDataId.setText(String.valueOf(sumOfOnline));
//                        }
//                        if (diningOut.equals(listNewEntry.get(i).getCategory())) {
//                            sumOfDiningOut += Double.parseDouble(listNewEntry.get(i).getAmount());
//                            Log.i(TAG, "onLoaded: onlineCategoryDiningOut" + sumOfDiningOut);
//                            mBinding.tvDiningOutId.setText(String.valueOf(sumOfDiningOut));
//                        }
//                        if (groceries.equals(listNewEntry.get(i).getCategory())) {
//                            sumOfGrocery += Double.parseDouble(listNewEntry.get(i).getAmount());
//                            Log.i(TAG, "onLoaded: onlineCategoryGroceries" + sumOfGrocery);
//                            mBinding.tvGroceryId.setText(String.valueOf(sumOfGrocery));
//                        }
//                        if (transport.equals(listNewEntry.get(i).getCategory())) {
//                            sumOfTransport += Double.parseDouble(listNewEntry.get(i).getAmount());
//                            Log.i(TAG, "onLoaded: onlineCategoryTransport" + sumOfTransport);
//                            mBinding.tvTransportId.setText(String.valueOf(sumOfTransport));
//                        }
//                        if (bills.equals(listNewEntry.get(i).getCategory())) {
//                            sumOfBills += Double.parseDouble(listNewEntry.get(i).getAmount());
//                            Log.i(TAG, "onLoaded: onlineCategoryBills" + sumOfBills);
//                            mBinding.tvBillsId.setText(String.valueOf(sumOfBills));
//                        }
//
//
//                        if (stTotalIncome.equals(listNewEntry.get(i).getType())) {
//                            totalIncome += Integer.parseInt(listNewEntry.get(i).getAmount());
//                        } else {
//                            totalExpense += Integer.parseInt(listNewEntry.get(i).getAmount());
//                        }
//
//                        Log.i(TAG, "onLoaded: totalIncomeAndTotalExpense" + "\n" + totalIncome + "\n" + totalExpense);
//
//                    } else {
//                        Toast.makeText(getContext(), "Graph Data Not Found", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//
//
//                sumOfAllValues = sumOfOnline + sumOfDiningOut + sumOfGrocery + sumOfTransport + sumOfBills;
//                Log.i(TAG, "onLoaded: sumOfAllValues" + sumOfAllValues);
//                perOfOnline = (sumOfOnline * 100) / sumOfAllValues;
//                perOfDiningOut = (sumOfDiningOut * 100) / sumOfAllValues;
//                perOfGrocery = (sumOfGrocery * 100) / sumOfAllValues;
//                perOfTransport = (sumOfTransport * 100) / sumOfAllValues;
//                perOfBills = (sumOfBills * 100) / sumOfAllValues;
//
////                Log.i(TAG, "onLoaded: percentageOfAllGivenValues" + "\n" + String.format("%.1f", perOfOnline) + "\n" + String.format("%.1f", perOfDiningOut) + "\n" + String.format("%.1f", perOfGrocery) + "\n" + String.format("%.1f", perOfTransport) + "\n" + String.format("%.1f", perOfBills));
//
//                mBinding.tvTotalIncomeId.setText(String.valueOf(totalIncome));
//                mBinding.tvTotalExpenseId.setText(String.valueOf(totalExpense));
//                PiaChart.attendancePiaChartGenerator(mBinding.attendanceReportGeneratorPiaChart, perOfOnline, perOfDiningOut, perOfGrocery, perOfTransport, perOfBills);
//            }
//
//            @Override
//            public void onFailure(String e) {
//                progressHUD.dismiss();
//                Log.i(TAG, "onFailure: Error" + e);
//            }
//
//            @Override
//            public void onEmpty() {
//                progressHUD.dismiss();
//                Toast.makeText(getContext(), "No Recent Transactions", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

}