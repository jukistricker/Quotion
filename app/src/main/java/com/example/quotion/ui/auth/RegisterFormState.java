package com.example.quotion.ui.auth;

public class RegisterFormState {
    private String usernameError;
    private String passwordError;
    private String confirmPasswordError;
    private boolean isDataValid;

    public RegisterFormState(String usernameError, String passwordError, String confirmPasswordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.isDataValid = false;
    }

    public RegisterFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }

    // Getter methods
    public String getUsernameError() { return usernameError; }
    public String getPasswordError() { return passwordError; }
    public String getConfirmPasswordError() { return confirmPasswordError; }
    public boolean isDataValid() { return isDataValid; }
}
