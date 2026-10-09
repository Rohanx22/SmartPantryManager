package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Custom DialogFragment allowing the user to pick an ingredient expiry date.
 * Modeled directly on Listing 4.10 in the MAD700 Learner Guide.
 */
public class DatePickerDialog extends DialogFragment {

    Calendar selectedDate;

    public interface SaveDateListener {
        void didFinishDatePickerDialog(Calendar selectedTime);
    }

    public DatePickerDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_date, container);

        if (getDialog() != null) {
            getDialog().setTitle("Select Expiry Date");
        }
        selectedDate = Calendar.getInstance();

        final CalendarView cv = view.findViewById(R.id.calendarView);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                selectedDate.set(year, month, day);
            }
        });

        Button saveButton = view.findViewById(R.id.buttonSelect);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem(selectedDate);
            }
        });

        Button cancelButton = view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

    private void saveItem(Calendar selectedTime) {
        SaveDateListener activity = (SaveDateListener) getActivity();
        if (activity != null) {
            activity.didFinishDatePickerDialog(selectedTime);
        }
        if (getDialog() != null) {
            getDialog().dismiss();
        }
    }
}
