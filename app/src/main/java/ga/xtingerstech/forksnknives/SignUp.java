package ga.xtingerstech.forksnknives;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ga.xtingerstech.forksnknives.common.Common;
import ga.xtingerstech.forksnknives.models.User;

public class SignUp extends AppCompatActivity {
    private  String phoneNumber;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        final EditText nameEditText = findViewById(R.id.sign_up_name);
        final EditText addressEditText = findViewById(R.id.sign_up_address);
        Button signUpBtn = findViewById(R.id.sign_up_submit_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameEditText.getText().toString().matches("")
                        && !addressEditText.getText().toString().matches("")){
                    currentUser = new User(nameEditText.getText().toString(), addressEditText.getText().toString());
                    saveData(currentUser, phoneNumber);
                    DatabaseReference users_database = FirebaseDatabase.getInstance().getReference().child("users");
                    users_database.child(phoneNumber).setValue(currentUser);
                    Intent menuHomeIntent = new Intent(SignUp.this, Home.class);
                    startActivity(menuHomeIntent);
                    finish();

                }else{
                    Toast.makeText(SignUp.this, "Please Completely Fill The Form",
                            Toast.LENGTH_LONG).show();
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
}
