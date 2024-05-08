package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex3.api.UserAPI;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText displayNameEditText;
    private ImageView profilePicImageView;
    private TextView profilePicPathTextView;
    private Button registerButton;
    private UserAPI userAPI;

    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        registerButton = findViewById(R.id.registerButton);
        Button loginBtn = findViewById(R.id.toLogin);
        loginBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        });

        // Password masking
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String displayName = displayNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            showToast("Please enter a username");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Please enter a password");
            return;
        }

        if (!isValidPassword(password)) {
            showToast("Password should have at least one numerical digit(0-9). Password's length should be in between 8 to 15 characters. Password should have at least one lowercase letter(a-z). Password should have at least one uppercase letter(A-Z)");
            return;
        }

        if (TextUtils.isEmpty(displayName)) {
            showToast("Please enter a display name");
            return;
        }

        // All fields are valid
        handleRegister();

    }

   //private boolean isValidPassword(String password) {
   //    // Password should have a minimum length of 8 characters and a maximum length of 15 characters
   //    if (password.length() < 8 || password.length() > 15) {
   //        return false;
   //    }

   //    // Password should contain at least one uppercase letter
   //    boolean containsUppercase = false;
   //    // Password should contain at least one lowercase letter
   //    boolean containsLowercase = false;
   //    // Password should contain at least one numerical digit
   //    boolean containsDigit = false;

   //    for (char c : password.toCharArray()) {
   //        if (Character.isUpperCase(c)) {
   //            containsUppercase = true;
   //        } else if (Character.isLowerCase(c)) {
   //            containsLowercase = true;
   //        } else if (Character.isDigit(c)) {
   //            containsDigit = true;
   //        }

   //        // Break the loop if all required character types are found
   //        if (containsUppercase && containsLowercase && containsDigit) {
   //            break;
   //        }
   //    }

   //    return containsUppercase && containsLowercase && containsDigit;
   //}

    private String getImagePathFromUri(Uri uri) {
        String imagePath = null;
        if (uri != null) {
            // Retrieve the image path using the ContentResolver
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                imagePath = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        return imagePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // The user has selected an image
            Uri imageUri = data.getData();

            // Get the selected image path
            String imagePath = getImagePathFromUri(imageUri);


            // Update the path TextView
            profilePicPathTextView.setText(imagePath);

            // Set the selected image to the ImageView
            profilePicImageView.setImageURI(imageUri);
        }
    }

    private void performRegistration(String username, String password, String displayName, String profilePicPath) {
        // Convert the image to base64 string
        //String base64Image = convertImageToBase64(profilePicPath); - error!

        // TODO: Add the user to the database!!
        //registerViewModel.createNewUser(username, password, displayName, profilePicPath);
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    private void handleRegister() {
        // Get values from the EditText fields
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String nickname = displayNameEditText.getText().toString().trim();


            userAPI = UserAPI.getInstance();
            CompletableFuture<String> future = userAPI.registerUser(username, password, nickname);
            System.out.println("1");
            future.thenAccept(status -> {
                if (status.equals("ok")) {
                    navigateToLogin();
                } else {
                    showToast(status);
                }
            }).exceptionally(ex -> {
                showToast("An error occurred: " + ex.getMessage());
                return null;
            });

    }


    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void navigateToLogin() {
        // Start the Register activity
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish(); // Finish the current activity so that the user cannot navigate back to it
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private String convertImageToBase64(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
