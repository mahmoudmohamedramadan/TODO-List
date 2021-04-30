package com.mahmoudramadan.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.mahmoudramadan.todolist.Utils.TODODatabaseHandler;

public class TaskDateActivity extends AppCompatDialogFragment {

    public static String TAG = "TaskDateDialog";
    private Button saveTaskDateButton;
    private DatePicker taskDatePicker;
    private View currentClickedItem;

    public TaskDateActivity (View currentClickedItem) {
        this.currentClickedItem = currentClickedItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.task_date, null);
        saveTaskDateButton = view.findViewById(R.id.saveTaskDateButton);
        taskDatePicker = view.findViewById(R.id.taskDatePicker);

        builder.setView(view).setCustomTitle(new TextView(getContext()));
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TODODatabaseHandler db = new TODODatabaseHandler(getActivity());
        db.openDatabase();

        final Bundle bundle = getArguments();

        saveTaskDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView selectedDateTextView = currentClickedItem.findViewById(R.id.selectedDateTextView);
                selectedDateTextView.setText(taskDatePicker.getYear() + "/" + taskDatePicker.getMonth() + "/" + taskDatePicker.getDayOfMonth());

                if (bundle != null) {
                    db.updateTaskDate(bundle.getInt("id"), selectedDateTextView.getText().toString());
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
