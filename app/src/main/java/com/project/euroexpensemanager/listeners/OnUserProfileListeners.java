package com.project.euroexpensemanager.listeners;

import com.project.euroexpensemanager.datamodel.SignUpDataModel;

public interface OnUserProfileListeners {
    public void onProfileLoaded(SignUpDataModel signUpDataModel);
    public void onFailed(String e);
    public void isEmpty();
}
