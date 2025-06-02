package com.example.quotion.ui.focus.timer;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quotion.R;
import com.example.quotion.ui.MainActivity;
import com.example.quotion.ui.focus.FocusSession;
import com.example.quotion.databinding.FragmentFocusTimerBinding;
import com.example.quotion.ui.focus.FocusViewModel;

import java.util.Locale;

public class FocusTimerFragment extends Fragment{
    private FragmentFocusTimerBinding binding;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;

    private long focusDurationInMillis;
    private long startTimeMillis = 0;
    private long timeLeftMillis = 0;

    private MediaPlayer mediaPlayer;
    private FocusViewModel viewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFocusTimerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FocusViewModel.class);

        viewModel.timeLeftMillis.observe(getViewLifecycleOwner(), millis -> {
            if (millis != null){
                timeLeftMillis = millis;
                updateTimerDisplay(timeLeftMillis);
                if (focusDurationInMillis == 0){
                    focusDurationInMillis = timeLeftMillis;
                }
                float progress = (float) timeLeftMillis * 100 / focusDurationInMillis;
                binding.circularProgressBar.setProgress(progress);
            }
        });

        viewModel.isRunning.observe(getViewLifecycleOwner(), running ->{
           isRunning =  running != null && running;
           binding.btnStartFocus.setText(isRunning ? "Stop Focus" : "Start Focus");
           binding.seekBarDuration.setEnabled(!isRunning);

            if (isRunning && countDownTimer == null) {
                // Nếu đang chạy nhưng chưa có CountDownTimer, tạo lại timer
                if (timeLeftMillis > 0) {
                    startTimer(timeLeftMillis);
                }else{
                    Log.e("FocusTimer", "Không thể bắt đầu timer: timeLeftMillis <= 0");
                }
            } else if (!isRunning && countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        });

        binding.seekBarDuration.setMax(180);
        binding.seekBarDuration.setProgress(25);
        binding.tvSelectedMinutes.setText("25 minutes");

        // SeekBar
        binding.seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 0) {
                    seekBar.setProgress(1);
                    binding.tvSelectedMinutes.setText("1 minutes");
                } else {
                    binding.tvSelectedMinutes.setText(progress + " minutes");
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Nút Start/Stop
        binding.btnStartFocus.setOnClickListener(v -> {
            if (!isRunning) {
                int minutes = binding.seekBarDuration.getProgress();
                if (minutes <= 0) {
                    Toast.makeText(getContext(), "Chọn thời gian > 0 phút", Toast.LENGTH_SHORT).show();
                    return;
                }
                startFocus(minutes);
            } else {
                stopFocusManually();
            }
        });
        // Nếu ViewModel chưa có giá trị, set mặc định
        if (viewModel.timeLeftMillis.getValue() == null) {
            timeLeftMillis = 25 * 60 * 1000L;
            focusDurationInMillis = timeLeftMillis;
            viewModel.setTimeLeftMillis(timeLeftMillis);
            viewModel.setIsRunning(false);
        }
    }

    private void startTimer(long timeLeftMillis) {
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                FocusTimerFragment.this.timeLeftMillis = millisUntilFinished; // cập nhật biến trường
                updateTimerDisplay(millisUntilFinished);
                float progress = (float) millisUntilFinished * 100 / focusDurationInMillis;
                binding.circularProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                updateTimerDisplay(0);
                binding.circularProgressBar.setProgress(0f);
                binding.btnStartFocus.setText("Start Focus");
                binding.seekBarDuration.setEnabled(true);

                playAlarmSound();
                vibratePattern();
                cancelFocusNotification();
                saveFocusSession(System.currentTimeMillis());
            }
        };
        countDownTimer.start();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void startFocus(int minutes) {

        focusDurationInMillis = minutes * 60 * 1000L;
        timeLeftMillis = focusDurationInMillis;
        startTimeMillis = System.currentTimeMillis();

        viewModel.setTimeLeftMillis(timeLeftMillis);
        viewModel.setIsRunning(true);

        startTimer(timeLeftMillis);
        showOngoingFocusNotification();

//        isRunning = true;
//        binding.btnStartFocus.setText("Stop Focus");
//        binding.seekBarDuration.setEnabled(false);
//
//        focusDurationInMillis = minutes * 60 * 1000L;
//        timeLeftMillis = focusDurationInMillis;
//        startTimeMillis = System.currentTimeMillis();
//
//        binding.circularProgressBar.setProgressWithAnimation(100f, 500L);
//
//        showOngoingFocusNotification();
//
//        countDownTimer = new CountDownTimer(focusDurationInMillis, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                timeLeftMillis = millisUntilFinished;
//                updateTimerDisplay(millisUntilFinished);
//
//                float progress = (float) millisUntilFinished * 100 / focusDurationInMillis;
//                binding.circularProgressBar.setProgress(progress);
//            }
//
//            @Override
//            public void onFinish() {
//                isRunning = false;
//                updateTimerDisplay(0);
//                binding.circularProgressBar.setProgress(0f);
//                binding.btnStartFocus.setText("Start Focus");
//                binding.seekBarDuration.setEnabled(true);
//
//                playAlarmSound();
//                vibratePattern();
//                cancelFocusNotification();
//                saveFocusSession(System.currentTimeMillis());
//            }
//        };
//        countDownTimer.start();
    }

    private void stopFocusManually() {
        if (countDownTimer != null) countDownTimer.cancel();

        isRunning = false;
        binding.btnStartFocus.setText("Start Focus");
        binding.seekBarDuration.setEnabled(true);

        long endTimeMillis = System.currentTimeMillis();
        updateTimerDisplay(0);
        binding.circularProgressBar.setProgress(0f);
        cancelFocusNotification();
        if (startTimeMillis > 0) {
            saveFocusSession(endTimeMillis);
        }
    }

    private void updateTimerDisplay(long millis) {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.tvTimer.setText(timeFormatted);
    }

    private void playAlarmSound() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.finish_audio);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

//    private void vibrateOnFinish() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            VibratorManager vibratorManager = (VibratorManager) requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
//            if (vibratorManager != null) {
//                Vibrator vibrator = vibratorManager.getDefaultVibrator();
//                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.));
//            }
//        } else {
//            Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
//            if (vibrator != null) {
//                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//            }
//        }
//    }

    private void vibratePattern() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            return; // Không có quyền rung
        }
        long[] timings = {0, 200, 100, 200, 100, 200, 300, 400, 100, 400, 100, 400};  // [rung, nghỉ, rung, nghỉ...]
        int[] amplitudes = {255, 0, 255, 0, 255, 0, 255, 0, 255, 0, 255, 0};          // Biên độ tương ứng

        VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, -1);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (vibratorManager != null) {
                vibratorManager.getDefaultVibrator().vibrate(effect);
            }
        } else {
            Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(effect);
            }
        }
    }
    private void createNotificationChannel() {

        String channelId = "focus_timer_channel";
        CharSequence name = "Focus Timer Channel";
        String description = "Thông báo khi đang tập trung";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void showOngoingFocusNotification() {
        createNotificationChannel();

        String channelId = "focus_timer_channel";

        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("open_fragment", "focus");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelId)
                .setSmallIcon(R.drawable.ic_default_avatar)
                .setContentTitle("Đang tập trung")
                .setContentText("Đừng rời mắt khỏi mục tiêu!")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1000, builder.build());
    }

    private void cancelFocusNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.cancel(1000);
    }




    private void saveFocusSession(long endTimeMillis) {
        FocusSession session = new FocusSession(startTimeMillis, endTimeMillis);
        viewModel.saveFocusSession(session);
        startTimeMillis = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        binding = null;
    }

}
