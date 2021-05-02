package com.mahmoudramadan.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.mahmoudramadan.todolist.Model.CategoryModel;
import com.mahmoudramadan.todolist.Utils.CategoryDatabaseHandler;

public class AddNewCategory extends AppCompatDialogFragment {

    public static String TAG = "AddNewCategoryDialog";
    private TextView newCategoryTitle;
    private EditText newCategoryEditText;
    private Button saveCategoryButton;

    public static AddNewCategory newInstance() {
        return new AddNewCategory();
    }

    private void setNewCategoryButtonColor(boolean isEnabled, int textColor) {
        saveCategoryButton.setEnabled(isEnabled);
        saveCategoryButton.setTextColor(textColor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_category, null);

        newCategoryTitle = view.findViewById(R.id.newCategoryTitle);
        newCategoryEditText = view.findViewById(R.id.newCategoryEditText);
        saveCategoryButton = view.findViewById(R.id.saveCategoryButton);

        setNewCategoryButtonColor(false, Color.GRAY);

        builder.setView(view).setCustomTitle(new TextView(getContext()));
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CategoryDatabaseHandler db = new CategoryDatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdated = false;
        final Bundle bundle = getArguments();

        if (bundle != null) {
            isUpdated = true;
            String task = bundle.getString("category");
            newCategoryTitle.setText("Edit Category");
            newCategoryEditText.setText(task);
            newCategoryEditText.setSelection(newCategoryEditText.getText().length());
        }

        newCategoryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) setNewCategoryButtonColor(false, Color.GRAY);
                else setNewCategoryButtonColor(true, Color.rgb(0, 153, 204));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdated = isUpdated;

        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newCategoryEditText.getText().toString();
                if (finalIsUpdated) {
                    db.updateCategoryText(bundle.getInt("id"), text);
                } else {
                    CategoryModel category = new CategoryModel();
                    category.setCategory(text);
                    db.insertCategory(category);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
}
