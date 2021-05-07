package com.mahmoudramadan.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mahmoudramadan.todo.Model.TODOModel;
import com.mahmoudramadan.todo.Utils.TODODatabaseHandler;

public class AddNewTask extends AppCompatDialogFragment {

    public static String TAG = "AddNewTaskDialog", category_id;
    private TextView newTaskTitle;
    private EditText newTaskEditText;
    private Button saveTaskButton;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    private void setNewCategoryButtonColor(Drawable borderColor, boolean isEnabled, int textColor) {
        newTaskEditText.setBackground(borderColor);
        saveTaskButton.setEnabled(isEnabled);
        saveTaskButton.setBackgroundColor(textColor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_task, null);

        newTaskTitle = view.findViewById(R.id.newTaskTitle);
        newTaskEditText = view.findViewById(R.id.newTaskEditText);
        saveTaskButton = view.findViewById(R.id.saveTaskButton);

        setNewCategoryButtonColor(getContext().getDrawable(R.drawable.disabled_edit_text_border), false, Color.GRAY);

        builder.setView(view).setCustomTitle(new TextView(getContext()));
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TODODatabaseHandler db = new TODODatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdated = false;
        final Bundle bundle = getArguments();

        if (bundle != null) {
            isUpdated = true;
            String task = bundle.getString("task");
            newTaskTitle.setText(getString(R.string.edit_task));
            newTaskEditText.setText(task);
            newTaskEditText.setSelection(newTaskEditText.getText().length());
        }

        newTaskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(""))
                    setNewCategoryButtonColor(getContext().getDrawable(R.drawable.disabled_edit_text_border), false, Color.GRAY);
                else
                    setNewCategoryButtonColor(getContext().getDrawable(R.drawable.enabled_edit_text_border), true, Color.rgb(0, 153, 204));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdated = isUpdated;

        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskEditText.getText().toString();
                if (finalIsUpdated) {
                    db.updateTaskText(bundle.getInt("id"), text);
                } else {
                    if (db.getTaskCount(text, Integer.parseInt(category_id)) < 1) {
                        TODOModel task = new TODOModel();
                        task.setTask(text);
                        task.setStatus(0);
                        task.setDate(null);
                        task.setCategory_id(Integer.parseInt(category_id));
                        db.insertTask(task);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewTask.this.getActivity());
                        builder.setTitle("Warning");
                        builder.setMessage("This task is already exists");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 130, 170));
                    }
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
