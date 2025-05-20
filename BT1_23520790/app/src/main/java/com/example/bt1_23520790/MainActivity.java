package com.example.bt1_23520790;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bt1_23520790.Model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        User user = (User) getIntent().getSerializableExtra("user");
        TextView userIdTV = findViewById(R.id.MNV_Textbox);
        TextView nameTV = findViewById(R.id.Name_Textbox);
        TextView addressTV = findViewById(R.id.Address_Textbox);
        TextView departmentTV = findViewById(R.id.Department_Textbox);

        userIdTV.setText(user.UserId);
        nameTV.setText(user.Name);
        addressTV.setText(user.Address);
        departmentTV.setText(user.Department);

        Button logOutButton = findViewById(R.id.LogOutButton);
        logOutButton.setOnClickListener(v -> LogOut());
    }

    private void LogOut(){
        TextView Name_Textbox = findViewById(R.id.Name_Textbox);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("name",Name_Textbox.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}