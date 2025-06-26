package com.example.quotion.ui.profile;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserRepository {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public UserProfile getUserProfile() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

            return new UserProfile(user.getDisplayName(), user.getEmail(), photoUrl);
        }
        return null;
    }

    public Task<Uri> uploadAvatarToStorage(Uri imageUri) {
        FirebaseUser user = getCurrentUser();
        if (user == null || imageUri == null) return Tasks.forException(new Exception("Thiếu thông tin"));

        StorageReference avatarRef = firebaseStorage.getReference().child("avatars/" + user.getUid() + ".jpg");
        return avatarRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return avatarRef.getDownloadUrl();
                });
    }

    public Task<Void> updateUserProfile(String displayName, Uri photoUri) {
        FirebaseUser user = getCurrentUser();
        if (user == null) return Tasks.forException(new Exception("Người dùng không tồn tại"));

        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        if (displayName != null) builder.setDisplayName(displayName);
        if (photoUri != null) builder.setPhotoUri(photoUri);

        return user.updateProfile(builder.build());
    }

    public Task<Void> changePassword(String newPassword) {
        FirebaseUser user = getCurrentUser();
        if (user == null) return Tasks.forException(new Exception("Người dùng không tồn tại"));
        return user.updatePassword(newPassword);
    }
}
