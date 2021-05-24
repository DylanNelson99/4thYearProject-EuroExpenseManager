package com.project.euroexpensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.project.euroexpensemanager.databinding.ActivityFullImageViewBinding;

//For camera image

public class FullImageViewActivity extends AppCompatActivity {
    private ActivityFullImageViewBinding mBinging;
    private String imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinging = DataBindingUtil.setContentView(this, R.layout.activity_full_image_view);
        try {
            imageProfile = getIntent().getStringExtra("imageProfile");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Glide
                .with(FullImageViewActivity.this)
                .load(imageProfile)
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.no_image)
                .into(mBinging.ivProfileId);

        mBinging.ivBackArrowId.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
}