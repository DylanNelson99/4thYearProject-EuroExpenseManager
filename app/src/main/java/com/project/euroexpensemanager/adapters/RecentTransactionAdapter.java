package com.project.euroexpensemanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.project.euroexpensemanager.EntriesDetailActivity;
import com.project.euroexpensemanager.R;
import com.project.euroexpensemanager.databinding.RecentTransactionCustomLayoutBinding;
import com.project.euroexpensemanager.datamodel.NewEntryModelClass;
import com.project.euroexpensemanager.fragments.ExpenseFragment;
import com.project.euroexpensemanager.fragments.GraphFragment;
import com.project.euroexpensemanager.fragments.HomeFragment;
import com.project.euroexpensemanager.fragments.IncomeFragment;

import java.util.List;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {
    private List<NewEntryModelClass> listNewEntry;
    private Context context;
    private DeleteSubItemInterface deleteSubItemInterface;

    public RecentTransactionAdapter(List<NewEntryModelClass> listNewEntry, Context context, ExpenseFragment graphFragment) {
        this.listNewEntry = listNewEntry;
        this.context = context;
        this.deleteSubItemInterface = (DeleteSubItemInterface) graphFragment;
    }

    public RecentTransactionAdapter(List<NewEntryModelClass> listNewEntry, Context context, IncomeFragment graphFragment) {
        this.listNewEntry = listNewEntry;
        this.context = context;
        this.deleteSubItemInterface = (DeleteSubItemInterface) graphFragment;
    }
    public RecentTransactionAdapter(List<NewEntryModelClass> listNewEntry, Context context, HomeFragment graphFragment) {
        this.listNewEntry = listNewEntry;
        this.context = context;
        this.deleteSubItemInterface = (DeleteSubItemInterface) graphFragment;
    }
    public interface DeleteSubItemInterface {
        void getDeleteInterface(NewEntryModelClass newEntryModelClass);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentTransactionCustomLayoutBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.recent_transaction_custom_layout, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        NewEntryModelClass modelClass = listNewEntry.get(position);
        modelClass.setTitle(modelClass.getTitle());
        modelClass.setType(modelClass.getType());
        vh.itemBinding.setItem(modelClass);

        vh.itemBinding.ivDeleteId.setOnClickListener(view -> {
            deleteSubItemInterface.getDeleteInterface(listNewEntry.get(position));
        });

        vh.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EntriesDetailActivity.class);
            intent.putExtra("EntriesObject", listNewEntry.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listNewEntry.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecentTransactionCustomLayoutBinding itemBinding;

        public ViewHolder(@NonNull RecentTransactionCustomLayoutBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }
    }
}
