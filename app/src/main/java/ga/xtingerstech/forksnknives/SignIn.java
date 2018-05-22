package ga.xtingerstech.forksnknives;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import ga.xtingerstech.forksnknives.common.Common;
import ga.xtingerstech.forksnknives.models.User;

public class SignIn extends AppCompatActivity {
    private String phoneNumber;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider phoneAuth = PhoneAuthProvider.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Button Handlers
        Button signInBtn = findViewById(R.id.sign_in_submit_btn);
        //Text View Handlers
        final TextView statusText = findViewById(R.id.sign_in_status);
        //Progress Bar Handlers
        final ProgressBar statusProgress = findViewById(R.id.sign_in_progress);
        //Input Handlers
        final EditText phoneNumberInput = findViewById(R.id.sign_in_phone_number);
        phoneNumberInput.setText(getData());
        phoneNumberInput.setSelection(getData().length());


        //When Submit Button Pressed
        //Event
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = phoneNumberInput.getText().toString();
                phoneNumberInput.setEnabled(false);
                phoneNumberInput.setFocusableInTouchMode(false);
                if(!phoneNumber.equals("+92")){
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText("Verifying Your Phone Number. Please Wait");
                    statusProgress.setIndeterminate(true);
                    statusProgress.setVisibility(View.VISIBLE);

                    phoneAuth.verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            SignIn.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                    statusText.setText("Verification Completed! Please Wait!");
                                    signInWithPhoneAuthCredential(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    phoneNumberInput.setEnabled(true);
                                    phoneNumberInput.setFocusableInTouchMode(true);
                                    statusProgress.setVisibility(View.GONE);
                                    statusText.setVisibility(View.GONE);
                                    if (e instanceof FirebaseAuthInvalidCredentialsException){
                                        Toast.makeText(getApplicationContext(),
                                                "Phone Number Format Not Valid!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else if(e instanceof FirebaseTooManyRequestsException){
                                        Toast.makeText(getApplicationContext(),
                                                "Error Sending Verification Code. Please Try Again",
                                                Toast.LENGTH_LONG).show();;
                                    }
                                    else if ( e instanceof FirebaseApiNotAvailableException){
                                        Toast.makeText(getApplicationContext(),
                                                "SMS Cannot Be Sent! Please Update Your Google Play Services",
                                                Toast.LENGTH_LONG).show();

                                    }else{
                                        Toast.makeText(getApplicationContext(),
                                                "Please Check Your Internet Connection!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    statusText.setText("Sms Sent To Your Phone Number! Please Wait");
                                }

                                @Override
                                public void onCodeAutoRetrievalTimeOut(String s) {
                                    phoneNumberInput.setEnabled(true);
                                    phoneNumberInput.setFocusableInTouchMode(true);
                                    super.onCodeAutoRetrievalTimeOut(s);
                                    statusText.setText("Did Not Receive SMS yet? Please Retry!");
                                    statusProgress.setVisibility(View.GONE);
                                }
                            });
                }
                else{
                    phoneNumberInput.setEnabled(true);
                    phoneNumberInput.setFocusableInTouchMode(true);
                    Toast.makeText(getApplicationContext(),
                            "Completely Fill the Form",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            // Sign in success, update UI with the signed-in user's information
                            DatabaseReference users_database = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber);
                            users_database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        saveData(dataSnapshot.getValue(User.class), phoneNumber);
                                        Intent menuHomeIntent = new Intent(SignIn.this, Home.class);
                                        startActivity(menuHomeIntent);
                                        finish();
                                    }else{
                                        Intent signUpIntent = new Intent(SignIn.this, SignUp.class);
                                        signUpIntent.putExtra("phoneNumber", phoneNumber);
                                        startActivity(signUpIntent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d("DATABASE ERROR", databaseError.getMessage());
                                }
                            });



                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("Sign In", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
    private void saveData(User u, String phoneNumber){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.sharedpref_user_data), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name",u.getName());
        editor.putString("address",u.getAddress());
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }
    private String getData(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.sharedpref_user_data), Context.MODE_PRIVATE);
        return sharedPref.getString("phoneNumber", "+92");
    }
}
