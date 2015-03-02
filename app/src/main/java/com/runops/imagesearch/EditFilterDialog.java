package com.runops.imagesearch;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.runops.imagesearch.model.MyPreferences;

import java.util.ArrayList;
import java.util.Arrays;

public class EditFilterDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private EditText mEditText;

    public interface EditFilterDialogListener {
        void onFinishEditFilterDialog(MyPreferences myPreferences);
    }

    public static EditFilterDialog newInstance(MyPreferences myPreferences) {
        EditFilterDialog frag = new EditFilterDialog();
        Bundle args = new Bundle();
        args.putSerializable("myPreferences", myPreferences);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_filter, container);

        // Set title
        getDialog().setTitle(getString(R.string.title_filter));

        final MyPreferences currentPreferences = (MyPreferences) getArguments().getSerializable("myPreferences");

        final Spinner spinnerImgsz = (Spinner) view.findViewById(R.id.spinner_imgsz);
        final Spinner spinnerImgtype = (Spinner) view.findViewById(R.id.spinner_imgtype);
        final Spinner spinnerColorfilter = (Spinner) view.findViewById(R.id.spinner_colorfilter);
        final EditText etSite = (EditText) view.findViewById(R.id.etSitefilter);

        // Hydrate spinners
        hydrateSpinner(view, R.id.spinner_imgsz, R.array.array_imgsz);
        hydrateSpinner(view, R.id.spinner_imgtype, R.array.array_imgtype);
        hydrateSpinner(view, R.id.spinner_colorfilter, R.array.array_colorfilter);

        spinnerImgsz.setSelection(currentPreferences.sizeIndex);
        spinnerImgtype.setSelection(currentPreferences.typeIndex);
        spinnerColorfilter.setSelection(currentPreferences.colorIndex);
        etSite.setText(currentPreferences.site);

        // Set button listeners
        Button applyButton = (Button) view.findViewById(R.id.btnApply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreferences newPreferences = new MyPreferences();
                newPreferences.size = spinnerImgsz.getSelectedItem().toString();
                newPreferences.sizeIndex = spinnerImgsz.getSelectedItemPosition();
                newPreferences.type = spinnerImgtype.getSelectedItem().toString();
                newPreferences.typeIndex = spinnerImgtype.getSelectedItemPosition();
                newPreferences.color = spinnerColorfilter.getSelectedItem().toString();
                newPreferences.colorIndex = spinnerColorfilter.getSelectedItemPosition();
                newPreferences.site = etSite.getText().toString();

                EditFilterDialogListener listener = (EditFilterDialogListener) getActivity();
                listener.onFinishEditFilterDialog(newPreferences);

                getDialog().dismiss();
            }
        });
        Button cancelButton = (Button) view.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    public void hydrateSpinner(View view, int viewResId, int arrayResId) {
        Spinner spinner = (Spinner) view.findViewById(viewResId);
        ArrayList<CharSequence> items = new ArrayList<CharSequence>(Arrays.asList(this.getResources().getTextArray(arrayResId)));
        // Add a default empty item
        items.add(0, "");
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
