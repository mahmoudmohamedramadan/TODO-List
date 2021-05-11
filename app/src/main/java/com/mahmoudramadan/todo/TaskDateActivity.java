package com.mahmoudramadan.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mahmoudramadan.todo.Utils.TODODatabaseHandler;

public class TaskDateActivity extends AppCompatDialogFragment {

    public static String TAG = "TaskDateDialog";
    private Button saveTaskDateTimeButton;
    private DatePicker taskDatePicker;
    private TimePicker taskTimePicker;
    private View currentClickedItem;

    public TaskDateActivity(View currentClickedItem) {
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

        saveTaskDateTimeButton = view.findViewById(R.id.saveTaskDateTimeButton);
        taskDatePicker = view.findViewById(R.id.taskDatePicker);
        taskTimePicker = view.findViewById(R.id.taskTimePicker);
        taskTimePicker.setIs24HourView(true);

        builder.setView(view).setCustomTitle(new TextView(getContext()));
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TODODatabaseHandler db = new TODODatabaseHandler(getActivity());
        db.openDatabase();

        final Bundle bundle = getArguments();

        saveTaskDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                TextView selectedDateTimeTextView = currentClickedItem.findViewById(R.id.selectedDateTimeTextView);
                int monthDate = taskDatePicker.getMonth() + 1;
                String dateTime = taskDatePicker.getYear() + "/" + monthDate + "/" + taskDatePicker.getDayOfMonth() + " " +
                        taskTimePicker.getCurrentHour() + ":" + taskTimePicker.getCurrentMinute();
                selectedDateTimeTextView.setText(dateTime);
                if (bundle != null) {
                    db.updateTaskDateTime(bundle.getInt("id"), dateTime);
                    db.pushNotification();
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
