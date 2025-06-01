package com.example.quotion.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quotion.R;
import com.example.quotion.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            binding.setUser(user);

            if (user.getPhotoUrl() != null) {
                Glide.with(requireContext())
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(binding.imageAvatar);
            }
        });

        viewModel.loadUserProfile();

        return binding.getRoot();
    }
}