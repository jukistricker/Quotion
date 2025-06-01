package com.example.quotion.ui.focus.timer;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quotion.R;
import com.example.quotion.ui.focus.FocusSession;
import com.example.quotion.databinding.FragmentFocusTimerBinding;
import com.example.quotion.ui.focus.FocusViewModel;

import java.util.Locale;

public class FocusTimerFragment extends Fragment{
    private FragmentFocusTimerBinding binding;
    private FocusViewModel viewModel;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;

    private long focusDurationInMillis;
    private long startTimeMillis = 0;
    private long timeLeftMillis = 0;

    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFocusTimerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FocusViewModel.class);

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
    }

    private void startFocus(int minutes) {
        isRunning = true;
        binding.btnStartFocus.setText("Stop Focus");
        binding.seekBarDuration.setEnabled(false);

        focusDurationInMillis = minutes * 60 * 1000L;
        timeLeftMillis = focusDurationInMillis;
        startTimeMillis = System.currentTimeMillis();

        binding.circularProgressBar.setProgressWithAnimation(100f, 500L);

        countDownTimer = new CountDownTimer(focusDurationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
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
                saveFocusSession(System.currentTimeMillis());
            }
        };
        countDownTimer.start();
    }

    private void stopFocusManually() {
        if (countDownTimer != null) countDownTimer.cancel();

        isRunning = false;
        binding.btnStartFocus.setText("Start Focus");
        binding.seekBarDuration.setEnabled(true);

        long endTimeMillis = System.currentTimeMillis();
        updateTimerDisplay(0);
        binding.circularProgressBar.setProgress(0f);

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
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
        });
        mediaPlayer.start();
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
