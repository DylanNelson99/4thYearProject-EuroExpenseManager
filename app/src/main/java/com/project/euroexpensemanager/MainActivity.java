package com.project.euroexpensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.project.euroexpensemanager.beforelogin.LoginActivity;
import com.project.euroexpensemanager.databinding.ActivityMainBinding;
import com.project.euroexpensemanager.fragments.ExpenseFragment;
import com.project.euroexpensemanager.fragments.GraphFragment;
import com.project.euroexpensemanager.fragments.HomeFragment;
import com.project.euroexpensemanager.fragments.IncomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        HomeFragment homeFragmentMain = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrameLayoutId, homeFragmentMain);
        transaction.commit();

			//Home page
        mBinding.tvHomeId.setOnClickListener(view -> {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transactionHome = getSupportFragmentManager().beginTransaction();
            transactionHome.replace(R.id.containerFrameLayoutId, homeFragment);
            transactionHome.commit();
        });
		//Income Page
        mBinding.tvIncomeId.setOnClickListener(view -> {
            IncomeFragment incomeFragmentListener = new IncomeFragment();
            FragmentTransaction transactionIncome = getSupportFragmentManager().beginTransaction();
            transactionIncome.replace(R.id.containerFrameLayoutId, incomeFragmentListener);
            transactionIncome.commit();
        });
		//Expense Page
        mBinding.tvExpenseId.setOnClickListener(view -> {
            ExpenseFragment expenseFragmentListener = new ExpenseFragment();
            FragmentTransaction transactionExpense = getSupportFragmentManager().beginTransaction();
            transactionExpense.replace(R.id.containerFrameLayoutId, expenseFragmentListener);
            transactionExpense.commit();
        });
		//Graph
        mBinding.tvGraphId.setOnClickListener(view -> {
            GraphFragment graphFragmentListener = new GraphFragment();
            FragmentTransaction transactionGraph = getSupportFragmentManager().beginTransaction();
            transactionGraph.replace(R.id.containerFrameLayoutId, graphFragmentListener);
            transactionGraph.commit();
        });
		//Add new entry
        mBinding.tvNewEntryId.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewEntryActivity.class));
        });
		//Log out button
        mBinding.ivLogoutId.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}