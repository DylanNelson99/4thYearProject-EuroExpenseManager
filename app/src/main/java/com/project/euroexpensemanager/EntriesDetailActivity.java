package com.project.euroexpensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.project.euroexpensemanager.databinding.ActivityEntriesDetailBinding;
import com.project.euroexpensemanager.datamodel.NewEntryModelClass;

public class EntriesDetailActivity extends AppCompatActivity {
    private ActivityEntriesDetailBinding mBinding;
    private NewEntryModelClass newEntryModelClass;
    private static final String TAG = "EntriesDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_entries_detail);
        newEntryModelClass = getIntent().getParcelableExtra("EntriesObject");
        Log.i(TAG, "onCreate: DataByModel" + newEntryModelClass.getTitle());


        Glide
                .with(EntriesDetailActivity.this)
                .load(newEntryModelClass.getImage())
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image)
                .into(mBinding.ivProfileId);
        mBinding.selectTypeSpinnerValueId.setText(newEntryModelClass.getType());
        mBinding.etTitleId.setText(newEntryModelClass.getTitle());
        mBinding.etDescriptionId.setText(newEntryModelClass.getDescription());
        mBinding.etDateId.setText(newEntryModelClass.getDate());
        mBinding.etTimeId.setText(newEntryModelClass.getTime());
        mBinding.etAmountId.setText(newEntryModelClass.getAmount());
        mBinding.selectCategorySpinnerValueId.setText(newEntryModelClass.getCategory());

        mBinding.ivBackArrowId.setOnClickListener(view -> {
            super.onBackPressed();
        });
        mBinding.ivProfileId.setOnClickListener(view -> {
            startActivity(new Intent(EntriesDetailActivity.this, FullImageViewActivity.class).putExtra("imageProfile", newEntryModelClass.getImage()));
        });
    }
}