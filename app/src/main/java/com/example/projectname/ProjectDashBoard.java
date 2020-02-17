package com.example.projectname;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProjectDashBoard extends AppCompatActivity {

    BottomNavigationView bottomNav;
    FormDetails formDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dash_board);

        formDetails = (FormDetails) getIntent().getSerializableExtra("FormDetails");

        bottomNav = findViewById(R.id.project_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.project_fragment_container,
                    new ProjectChat(ProjectDashBoard.this, formDetails)).commit();
        }

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.project_chat:
                            selectedFragment = new ProjectChat(ProjectDashBoard.this, formDetails);
                            break;
                        case R.id.project_resources:
                            selectedFragment = new ProjectResources(ProjectDashBoard.this, formDetails);
                            break;
                        case R.id.project_details:
                            selectedFragment = new ProjectDetails(ProjectDashBoard.this, formDetails);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.project_fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
}
