package com.example.yoavgray.nytarticlefinder.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yoavgray.nytarticlefinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterFragment extends DialogFragment implements DatePickerFragment.DateSelectedListener{
    private static final String BEGIN_DATE = "beginDate";
    private static final String END_DATE = "endDate";
    private static final String SORT_ID = "sort";

    String beginDate, endDate;
    int sortId;

    @BindView(R.id.text_view_from_date) TextView beginDateTextView;
    @BindView(R.id.text_view_to_date) TextView endDateTextView;
    @BindView(R.id.spinner_sort) Spinner spinner;

    private PrefParamsSelectedListener selectionListener;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(String beginDate, String endDate, int sortId) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(BEGIN_DATE, beginDate);
        args.putString(END_DATE, endDate);
        args.putInt(SORT_ID, sortId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The call when a date is saved
    public void onPrefParamsSelected(String beginDate, String endDate, int sortId) {
        if (selectionListener != null) {
            selectionListener.onPrefParamsSaved(beginDate, endDate, sortId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setupSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(sortId);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortId = spinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        getDialog().setTitle("Filter Attributes");
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        beginDate    = getArguments().getString(BEGIN_DATE);
        endDate      = getArguments().getString(END_DATE);
        sortId       = getArguments().getInt(SORT_ID);

        beginDateTextView.setText(getNicerDateFormat(beginDate));
        endDateTextView.setText(getNicerDateFormat(endDate));
        setupSpinner();
    }

    private String getNicerDateFormat(String date) {
        if (date != null) {
            StringBuilder showDate = new StringBuilder();
            showDate.append(date.substring(4, 6)).append("/");
            showDate.append(date.substring(6, 8)).append("/");
            showDate.append(date.substring(0, 4));
            return showDate.toString();
        } else {
            return "Any";
        }
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof PrefParamsSelectedListener) {
            selectionListener = (PrefParamsSelectedListener) context;
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

    @Override
    public void onDateSelected(int year, int month, int day, String whichDate) {
        // Format the date to what we're used to
        StringBuilder chosenDateToShow = new StringBuilder();
        chosenDateToShow.append(month < 10 ? "0" + month : month).append("/");
        chosenDateToShow.append(day < 10 ? "0" + day : day).append("/");
        chosenDateToShow.append(year);
        // Format date like it should be for the query parameter
        StringBuilder chosenDateToSend = new StringBuilder();
        chosenDateToSend.append(year);
        chosenDateToSend.append(month < 10 ? "0" + month : month);
        chosenDateToSend.append(day < 10 ? "0" + day : day);

        if (whichDate.equals("from")) {
            beginDateTextView.setText(chosenDateToShow.toString());
            beginDate = chosenDateToSend.toString();
        } else {
            endDateTextView.setText(chosenDateToShow.toString());
            endDate = chosenDateToSend.toString();
        }
    }

    public interface PrefParamsSelectedListener {
        void onPrefParamsSaved(String beginDate, String endDate, int sortId);
    }

    @OnClick(R.id.text_view_from_date)
    public void pickFromDate() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("", "from");
        datePickerFragment.setTargetFragment(FilterFragment.this, 300);
        datePickerFragment.show(fm, "fragment_date_picker");
    }

    @OnClick(R.id.text_view_to_date)
    public void pickToDate() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance("", "to");
        datePickerFragment.setTargetFragment(FilterFragment.this, 300);
        datePickerFragment.show(fm, "fragment_date_picker");
    }

    @OnClick(R.id.button_save_filters)
    public void returnFilters() {
        onPrefParamsSelected(beginDate, endDate, sortId);
        dismiss();
    }
}
