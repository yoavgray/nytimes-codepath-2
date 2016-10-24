package com.example.yoavgray.nytarticlefinder.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.example.yoavgray.nytarticlefinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DatePickerFragment extends DialogFragment {
    public final static String WHICH_DATE = "whichDate";
    public final static String DATE = "date";

    String whichDate;

    @BindView(R.id.date_picker) DatePicker datePicker;

    private DatePickerFragment.DateSelectedListener selectionListener;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    public static DatePickerFragment newInstance(String date, String whichDate) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(DATE, date);
        args.putString(WHICH_DATE, whichDate);
        fragment.setArguments(args);
        return fragment;
    }

    // The call when a sort parameter is clicked.
    public void onDateSelected(int year, int month, int day, String whichDate) {
        if (selectionListener != null) {
            selectionListener.onDateSelected(year, month, day, whichDate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        whichDate = getArguments().getString(WHICH_DATE, "from");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerFragment.DateSelectedListener) {
            selectionListener = (DatePickerFragment.DateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectionListener = null;
    }

    public interface DateSelectedListener {
        void onDateSelected(int year, int month, int day, String whichDate);
    }

    @OnClick(R.id.button_save_date)
    public void sendSavedDate() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        DateSelectedListener listener = (DateSelectedListener) getTargetFragment();
        listener.onDateSelected(year, month, day, whichDate);
        dismiss();
    }
}
