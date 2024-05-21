package gmail.developer_formal.freeappblocker.fragments;

import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.receivers.MyAdminReceiver;

public class StrictFragment extends Fragment {

    private Button actionButton, pickerButton;
    private TextView displayTimer;
    private LottieAnimationView waveAnimationView;
    private final static int REQUEST_CODE_ENABLE_ADMIN = 13545201;
    private BroadcastReceiver myReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String updateSecondsText = intent.getStringExtra("updateSecondsText");

                if(updateSecondsText != null && displayTimer != null)
                    displayTimer.setText(updateSecondsText);

                String resetUI = intent.getStringExtra("resetUI");

                if(resetUI == null)
                    return;

                if(displayTimer != null)
                    displayTimer.setText(AppUtils.convertToDHMS(BlockersManager.getInstance(context).getStrictDelay() / 1000));

                if(actionButton == null)
                    return;

                updateWaterEffect(false);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BlockersManager blockersManager = BlockersManager.getInstance(this.getContext());
        View view = inflater.inflate(R.layout.fragment_strict, container, false);

        actionButton = view.findViewById(R.id.actionButton);
        pickerButton = view.findViewById(R.id.pickerButton);
        displayTimer = view.findViewById(R.id.displayTimer);
        waveAnimationView = view.findViewById(R.id.waveAnimationView);

        displayTimer.setText(AppUtils.convertToDHMS(blockersManager.isStrictModeEnabled() ? blockersManager.getTotalStrictSecondsRemaining() : blockersManager.getStrictDelay() / 1000));

        updateWaterEffect(blockersManager.isStrictModeEnabled());

        pickerButton.setOnClickListener(v -> {
            new DateTimePickerFragment(displayTimer).show(getFragmentManager(), "dateTimePicker");
        });

        actionButton.setOnClickListener(v -> {
            if (!hasAdminPermission())
                showAdminRequestDialog();
            else
                showConfirmationDialog(actionButton);
        });
        return view;
    }

    private boolean hasAdminPermission() {
        Context context = this.getContext();
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(context, MyAdminReceiver.class);
        return devicePolicyManager.isAdminActive(adminComponent);
    }

    private void showAdminRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text, null);
        TextView text = view.findViewById(R.id.dialog_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText("Hi there! Strict mode requires admin permissions for the features below to work properly:\n\n" +
                "1. **Focus Mode**: Strict mode is designed to keep you on track by blocking distractions. You won't be able to turn off the blockers, which helps you stay focused on your tasks, and prevents temptation.\n\n" +
                "2. **Uninstall Protection**: Prevents you from uninstalling the app.\n\n" +
                "3. **No Force Stops**: Stops you from force-stopping the app.\n\n" +
                "Admin permissions are only used to ensure that strict mode works. No sensitive information is being accessed, or shared.");


        Button continueButton = view.findViewById(R.id.dialogTextContinueButton);
        Button cancelButton = view.findViewById(R.id.dialogTextCancelButton);
        cancelButton.setText("Cancel");
        builder.setView(view);
        AlertDialog dialog = builder.create();

        continueButton.setOnClickListener(v -> {
            dialog.dismiss();
            requestAdminPermission();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showConfirmationDialog(Button actionButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text, null);
        TextView text = view.findViewById(R.id.dialog_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText("Strict Mode is a powerful feature designed to provide an extra layer of protection against distractions. It's perfect for those moments when you might be tempted to turn off your blockers but need help staying focused.\n\n" +
                "When activated, Strict Mode prevents you from removing blockers, unmarking blocked apps, or disabling blockers. Additionally, you won’t be able to access your settings or uninstall the app while Strict Mode is active.\n\n" +
                "Please note that once you enable Strict Mode, you won't be able to turn it off until the timer runs out. Make sure to double-check that you've set the timer correctly before proceeding.");


        Button continueButton = view.findViewById(R.id.dialogTextContinueButton);
        Button cancelButton = view.findViewById(R.id.dialogTextCancelButton);
        cancelButton.setText("Cancel");
        builder.setView(view);
        AlertDialog dialog = builder.create();

        continueButton.setOnClickListener(v -> {
            BlockersManager.getInstance(this.getContext()).setStartedAt(System.currentTimeMillis());
            BlockersManager.getInstance(this.getContext()).changeStrictMode(true);
            updateWaterEffect(true);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void requestAdminPermission() {
        ComponentName adminComponent = new ComponentName(this.getContext(), MyAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Strict mode functionality.");
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }

    private void updateWaterEffect(boolean isActive) {
        if (isActive) {
            waveAnimationView.setAnimation(R.raw.wave_animation_enable);
            actionButton.setClickable(false);
            actionButton.setEnabled(false);
            actionButton.setText("STRICT MODE ACTIVE");
            pickerButton.setText("EXTEND TIMER");
            pickerButton.setBackgroundResource(R.drawable.red_border_background);

        } else {
            waveAnimationView.setAnimation(R.raw.wave_animation_disable);
            actionButton.setClickable(true);
            actionButton.setEnabled(true);
            actionButton.setText("ACTIVATE");
            pickerButton.setText("SET TIMER");
            pickerButton.setBackgroundResource(R.drawable.button_transparent_outline);
        }
        waveAnimationView.playAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("gmail.developer_formal.freeappblocker.UPDATE_STRICT_UI");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requireActivity().registerReceiver(myReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else {
            requireActivity().registerReceiver(myReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(myReceiver);
    }

}
