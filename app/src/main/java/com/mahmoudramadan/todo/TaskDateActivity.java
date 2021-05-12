package com.mahmoudramadan.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.mahmoudramadan.todo.Utils.TODODatabaseHandler;

public class TaskDateActivity extends AppCompatDialogFragment {

    public static String TAG = "TaskDateDialog";
    private TextView taskDateTimeTitle;
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
        super.onCreateDialog(savedInstanceState);

        TODODatabaseHandler db = new TODODatabaseHandler(getActivity());
        db.openDatabase();

        final Bundle bundle = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.task_date, null);

        taskDateTimeTitle = view.findViewById(R.id.taskDateTimeTitle);
        taskDatePicker = view.findViewById(R.id.taskDatePicker);
        taskTimePicker = view.findViewById(R.id.taskTimePicker);
        taskTimePicker.setIs24HourView(true);

        builder.setView(view).setCustomTitle(new TextView(getContext()));
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
}
