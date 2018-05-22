package ga.xtingerstech.forksnknives;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button Handler Variables (Main)
        Button signInBtn = findViewById(R.id.sign_in_btn);

        /*When User will Click On Sign In Or Sign Up Following Actions Will be Triggered*/
        //Sign In Click Event Listener
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(MainActivity.this,SignIn.class);
                startActivity(signInIntent);
            }
        });


    }
}
