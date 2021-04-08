package com.yunitski.fixedassets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ChangeDialog extends DialogFragment implements View.OnClickListener {
    Button ok, cancel;
    EditText editText;

    Communicator communicator;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        communicator = (Communicator) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_dialog, null);
        ok = view.findViewById(R.id.ok_btn);
        cancel = view.findViewById(R.id.cancel_btn);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        editText = view.findViewById(R.id.et_change);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok_btn:
                if (!editText.getText().toString().isEmpty()) {
                    communicator.onDialogMessage(editText.getText().toString());
                } else {
                    Toast.makeText(getContext(), "no data", Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;
            case R.id.cancel_btn:
                dismiss();
                Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    interface Communicator {
        void onDialogMessage(String message);
    }
}
