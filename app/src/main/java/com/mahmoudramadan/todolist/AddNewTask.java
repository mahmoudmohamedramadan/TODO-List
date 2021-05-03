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

import com.mahmoudramadan.todolist.Model.TODOModel;
import com.mahmoudramadan.todolist.Utils.TODODatabaseHandler;

public class AddNewTask extends AppCompatDialogFragment {

    public static String TAG = "AddNewTaskDialog", category_id;
    private TextView newTaskTitle;
    private EditText newTaskEditText;
    private Button saveTaskButton;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    private void setNewTaskButtonColor(boolean isEnabled, int textColor) {
        saveTaskButton.setEnabled(isEnabled);
        saveTaskButton.setTextColor(textColor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_task, null);

        newTaskTitle = view.findViewById(R.id.newTaskTitle);
        newTaskEditText = view.findViewById(R.id.newTaskEditText);
        saveTaskButton = view.findViewById(R.id.saveTaskButton);

        setNewTaskButtonColor(false, Color.GRAY);

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
            newTaskTitle.setText("Edit Task");
            newTaskEditText.setText(task);
            newTaskEditText.setSelection(newTaskEditText.getText().length());
        }

        newTaskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) setNewTaskButtonColor(false, Color.GRAY);
                else setNewTaskButtonColor(true, Color.rgb(0, 153, 204));
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
