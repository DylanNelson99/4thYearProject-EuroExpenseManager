package com.project.euroexpensemanager.listeners;

import com.project.euroexpensemanager.datamodel.NewEntryModelClass;

import java.util.List;

public interface OnGetEntriesListener {
    public void onLoaded(List<NewEntryModelClass> listNewEntry);
    public void onFailure(String e);
    public void onEmpty();
}
