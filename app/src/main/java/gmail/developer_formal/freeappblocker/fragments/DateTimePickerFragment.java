package gmail.developer_formal.freeappblocker.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;

public class DateTimePickerFragment extends DialogFragment {
    private final TextView displayTimer;
    private EditText daysInput, hoursInput, minutesInput;

    public DateTimePickerFragment(TextView displayTimer) {
        this.displayTimer = displayTimer;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();

        if(activity == null)
            return null;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_time_dialog, null);

        daysInput = dialogView.findViewById(R.id.daysInput);
        hoursInput = dialogView.findViewById(R.id.hoursInput);
        minutesInput = dialogView.findViewById(R.id.minutesInput);
        Button okButton = dialogView.findViewById(R.id.okButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        setupInputWatcher(daysInput, 7);
        setupInputWatcher(hoursInput, 23);
        setupInputWatcher(minutesInput, 59);

        long alreadySeconds = BlockersManager.getInstance(activity).getStrictDelay() / 1000;

        //////////////////////////////////////////

        long d = alreadySeconds / (24 * 3600);
        alreadySeconds %= (24 * 3600);

        if(d > 0)
            daysInput.setText(String.valueOf(d));

        //////////////////////////////////////////

        long h = alreadySeconds / 3600;
        alreadySeconds %= 3600;

        if(h > 0)
            hoursInput.setText(String.valueOf(h));

        //////////////////////////////////////////

        long m = alreadySeconds / 60;

        if(m > 0)
            minutesInput.setText(String.valueOf(m));


        okButton.setOnClickListener(v -> {
            int days = AppUtils.getInt(daysInput.getText().toString(), 0);
            int hours = AppUtils.getInt(hoursInput.getText().toString(), 0);
            int minutes = AppUtils.getInt(minutesInput.getText().toString(), 0);

            int totalSeconds = days * 24 * 60 * 60 + hours * 60 * 60 + minutes * 60;

            if(totalSeconds < 0)
                return;

            if (totalSeconds > 604800) {
                Toast.makeText(getActivity(), "Cannot select more than 7 days.", Toast.LENGTH_SHORT).show();
                return;
            }

            BlockersManager blockersManager = BlockersManager.getInstance(activity);

            if(!blockersManager.isStrictModeEnabled()){
                blockersManager.setStrictDelay(totalSeconds * 1000);
                displayTimer.setText(AppUtils.convertToDHMS(totalSeconds));
                dismiss();
                return;
            }

            long startedAt = blockersManager.getStartedAt();

            if(blockersManager.getTotalStrictSecondsRemaining() + totalSeconds > 604800) {
                Toast.makeText(getActivity(), "Cannot select more than 7 days.", Toast.LENGTH_SHORT).show();
                return;
            }

            blockersManager.setStartedAt(startedAt + (totalSeconds * 1000));
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(dialogView);
        return builder.create();
    }

    private void setupInputWatcher(EditText input, int maxValue) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int value = AppUtils.getInt(s.toString(), 0);

                if (value <= maxValue)
                    return;

                input.setText(String.valueOf(maxValue));
                input.setSelection(input.getText().length());
            }
        });
    }
}