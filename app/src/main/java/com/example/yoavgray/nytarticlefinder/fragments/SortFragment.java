package com.example.yoavgray.nytarticlefinder.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.example.yoavgray.nytarticlefinder.R;

import butterknife.BindString;

public class SortFragment extends DialogFragment {
    private static final String SORT_ID = "sortId";
    private static final String TITLE = "Sort Articles";
    private static final String[] sortOptions = { "Newest First", "Oldest First" };

    @BindString(R.string.sort_publish_date_newest_first) String newestFirstString;
    @BindString(R.string.sort_publish_date_oldest_first) String oldestFirstString;

    int sortId;

    private SortParamSelectedListener selectionListener;

    public SortFragment() {
        // Required empty public constructor
    }

    public static SortFragment newInstance(int sortId) {
        SortFragment fragment = new SortFragment();
        Bundle args = new Bundle();
        args.putInt(SORT_ID, sortId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortId = getArguments().getInt(SORT_ID);
        }
    }

    // The call when a sort parameter is clicked.
    public void onSortParamSelected(int sortId) {
        if (selectionListener != null) {
            selectionListener.onSortParamSelected(sortId);
        }
    }

    @Override @Nullable
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (isAdded()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(TITLE);

            builder.setSingleChoiceItems(sortOptions, sortId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    selectionListener = (SortParamSelectedListener) getActivity();
                    onSortParamSelected(position);

                    dialog.dismiss();
                }
            });

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

            return dialog;
        } else {
            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SortParamSelectedListener) {
            selectionListener = (SortParamSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SortParamSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectionListener = null;
    }

    public interface SortParamSelectedListener {
        void onSortParamSelected(int sortId);
    }
}
