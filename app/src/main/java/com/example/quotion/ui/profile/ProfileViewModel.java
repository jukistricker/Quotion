package com.example.quotion.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

            userProfile.setValue(new UserProfile(name, email, photoUrl));
        }
    }

}
