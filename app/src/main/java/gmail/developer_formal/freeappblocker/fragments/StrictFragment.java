package gmail.developer_formal.freeappblocker.fragments;

import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import gmail.developer_formal.freeappblocker.important.AppUtils;
import gmail.developer_formal.freeappblocker.important.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.receivers.MyAdminReceiver;
import org.jetbrains.annotations.NotNull;

public class StrictFragment extends Fragment {

    private Button actionButton, pickerButton;
    private TextView displayTimer;
    private LottieAnimationView waveAnimationView;
    private final static int REQUEST_CODE_ENABLE_ADMIN = 13545201;
    private BroadcastReceiver myReceiver;
    private AlertDialog dialog;

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

                Log.d("SDFDSFSFSDFSDF", intent.getStringExtra("updateSecondsText")+" ");
                Log.d("SDFDSFSFSDFSDF", intent.getStringExtra("resetUI") + " ");

                if(resetUI == null || resetUI.isEmpty())
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
    @SuppressWarnings("deprecation")
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getContext() == null || getFragmentManager() == null)
            return null;

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
                showConfirmationDialog();
        });
        return view;
    }

    private boolean hasAdminPermission() {
        Context context = this.getContext();

        if(context == null)
            return false;

        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(context, MyAdminReceiver.class);
        return devicePolicyManager.isAdminActive(adminComponent);
    }

    private void showAdminRequestDialog() {
        if(getContext() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text, null);
        TextView text = view.findViewById(R.id.dialog_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(R.string.admin_permission_motive_text);


        Button continueButton = view.findViewById(R.id.dialogTextContinueButton);
        Button cancelButton = view.findViewById(R.id.dialogTextCancelButton);
        cancelButton.setText(R.string.cancel_button_text);
        builder.setView(view);
        dialog = builder.create();

        continueButton.setOnClickListener(v -> {
            dialog.dismiss();
            BlockersManager.getInstance(getContext()).setPause(true);
            requestAdminPermission();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showConfirmationDialog() {
        if(getContext() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text, null);
        TextView text = view.findViewById(R.id.dialog_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(R.string.strict_mode_enable_warning);


        Button continueButton = view.findViewById(R.id.dialogTextContinueButton);
        Button cancelButton = view.findViewById(R.id.dialogTextCancelButton);
        cancelButton.setText(R.string.cancel_button_text);
        builder.setView(view);
        dialog = builder.create();

        BlockersManager blockersManager = BlockersManager.getInstance(this.getContext());

        continueButton.setOnClickListener(v -> {
            if(blockersManager.isStrictModeEnabled()){
                dialog.dismiss();
                return;
            }

            blockersManager.setStartedAt(System.currentTimeMillis());
            blockersManager.changeStrictMode(this.getContext(), true);
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
            actionButton.setText(R.string.strict_mode_active_message);
            pickerButton.setText(R.string.strict_mode_timer_active_message);
            pickerButton.setBackgroundResource(R.drawable.red_border_background);

        } else {
            waveAnimationView.setAnimation(R.raw.wave_animation_disable);
            actionButton.setClickable(true);
            actionButton.setEnabled(true);
            actionButton.setText(R.string.strict_mode_not_active_message);
            pickerButton.setText(R.string.strict_mode_timer_not_active_message);
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
        else
            requireActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(myReceiver);

        if(dialog == null)
            return;

        dialog.dismiss();
        dialog = null;
    }

}
