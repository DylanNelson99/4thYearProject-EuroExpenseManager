package com.project.euroexpensemanager.listeners;


public interface OnGetUsersEmailListener {
    public void onComplete(String email);
    public void onFailure(String e);
}
