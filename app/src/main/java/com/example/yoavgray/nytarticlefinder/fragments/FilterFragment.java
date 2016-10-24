package com.example.yoavgray.nytarticlefinder.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.yoavgray.nytarticlefinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterFragment extends DialogFragment {
    private static final String BEGIN_DATE = "beginDate";
    private static final String END_DATE = "endDate";

    @BindView(R.id.text_view_from_date) TextView beginDateTextView;
    @BindView(R.id.text_view_to_date) TextView endDateTextView;

    private PrefParamsSelectedListener selectionListener;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(String beginDate, String endDate) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(BEGIN_DATE, beginDate);
        args.putString(END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // The call when a sort parameter is clicked.
    public void onPrefParamsSelected(String beginDate, String endDate) {
        if (selectionListener != null) {
            selectionListener.onPrefParamsSaved(beginDate, endDate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        getDialog().setTitle("Filter Attributes");
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        String beginDate    = getArguments().getString(BEGIN_DATE);
        String endDate      = getArguments().getString(END_DATE);
        beginDateTextView.setText(beginDate == null ? "Any" : beginDate);
        endDateTextView.setText(endDate == null ? "Any" : endDate);
    }


    @Override
    public void onAttach(Context context) {
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

    public interface PrefParamsSelectedListener {
        void onPrefParamsSaved(String beginDate, String endDate);
    }
}
