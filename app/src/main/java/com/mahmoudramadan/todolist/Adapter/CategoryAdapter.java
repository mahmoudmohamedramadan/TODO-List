package com.mahmoudramadan.todolist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudramadan.todolist.AddNewCategory;
import com.mahmoudramadan.todolist.MainActivity;
import com.mahmoudramadan.todolist.Model.CategoryModel;
import com.mahmoudramadan.todolist.R;
import com.mahmoudramadan.todolist.TasksActivity;
import com.mahmoudramadan.todolist.Utils.CategoryDatabaseHandler;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryList;
    private MainActivity activity;
    private CategoryDatabaseHandler db;

    public CategoryAdapter(CategoryDatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_layout, parent, false);

        return new CategoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
        db.openDatabase();
        final CategoryModel item = categoryList.get(position);
        holder.categoryButton.setText(item.getCategory());

        holder.categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TasksActivity.class);
                intent.putExtra("category", item.getCategory());
                intent.putExtra("category_id", String.valueOf(item.getId()));
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setCategories(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public void updateItemCategoryText(int position) {
        CategoryModel item = categoryList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("category", item.getCategory());
        AddNewCategory fragment = new AddNewCategory();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewCategory.TAG);
    }

    public void deleteItem(int position) {
        CategoryModel item = categoryList.get(position);
        db.deleteCategory(item.getId());
        categoryList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button categoryButton;

        public ViewHolder(View view) {
            super(view);
            categoryButton = view.findViewById(R.id.categoryButton);
        }
    }
}