package com.example.quotion.ui.auth;

import android.content.Context;

import com.example.quotion.BuildConfig;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;

    public AuthRepository(Context context) {
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID
                ) // lấy từ Firebase console
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public Task<AuthResult> registerOrLoginWithEmail(String email, String password) {
        Task<AuthResult> registerTask = firebaseAuth.createUserWithEmailAndPassword(email, password);

        return registerTask.continueWithTask(task -> {
            if (task.isSuccessful()) {
                return Tasks.forResult(task.getResult());
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseAuthUserCollisionException) {
                    return firebaseAuth.signInWithEmailAndPassword(email, password);
                } else {
                    throw e;
                }
            }
        });
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public Task<AuthResult> signInWithCredential(AuthCredential credential) {
        return firebaseAuth.signInWithCredential(credential);
    }

}
