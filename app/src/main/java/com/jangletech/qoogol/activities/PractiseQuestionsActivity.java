package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.QuestionSection;
import com.jangletech.qoogol.model.Question;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class PractiseQuestionsActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise_questions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        prepareQuestions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practise_questions, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void prepareQuestions(){
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        Question question = new Question(1,"If x is a prime number, LCM of x and its successive number would be");
        Question question1 = new Question(2,"If x is a prime number, LCM of x and its successive number would be");
        Question question2 = new Question(3,"If x is a prime number, LCM of x and its successive number would be");
        Question question3 = new Question(4,"If x is a prime number, LCM of x and its successive number would be");
        List<Question> contactList = new ArrayList<>();
        contactList.add(question);
        contactList.add(question1);
        contactList.add(question2);
        contactList.add(question3);
        // Add your Sections
        sectionAdapter.addSection(new QuestionSection("Science",contactList));
        sectionAdapter.addSection(new QuestionSection("Algebra",contactList));
        sectionAdapter.addSection(new QuestionSection("Mathematics",contactList));
        sectionAdapter.addSection(new QuestionSection("Geometry",contactList));
        sectionAdapter.addSection(new QuestionSection("Social Science",contactList));
        sectionAdapter.addSection(new QuestionSection("Economy",contactList));
        sectionAdapter.addSection(new QuestionSection("Biology",contactList));
        sectionAdapter.addSection(new QuestionSection("Computer Science",contactList));
        sectionAdapter.addSection(new QuestionSection("Mechanical",contactList));

        // Set up your RecyclerView with the SectionedRecyclerViewAdapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapter);


    }
}
