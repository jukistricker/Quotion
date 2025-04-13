package com.example.quotion.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quotion.R;

import java.util.HashMap;

public class AddQuoteActivity extends AppCompatActivity {

    private EditText quoteEditText;
    private EditText authorEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_quote);
        //bind view
        quoteEditText=(EditText) findViewById(R.id.editTextQuote);
        authorEditText=(EditText) findViewById(R.id.editTextAuthor);
        addButton=(Button) findViewById(R.id.addButton);

        //listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text
                String quote = quoteEditText.getText().toString();
                String author = authorEditText.getText().toString();

                //check if empty
                if (quote.isEmpty()){
                    quoteEditText.setError("Cannot be empty");
                    return;
                }
                if (author.isEmpty()){
                    authorEditText.setError("Cannot be empty");
                    return;
                }

                //add to db
                addQuoteToDB(quote,author);

            }
        });

    }

    private void addQuoteToDB(String quote, String author) {
        //create a hashmap
        HashMap<String, Object> quoteHashMap = new HashMap<>();
        quoteHashMap.put("quote",quote);
        quoteHashMap.put("author",author);
        //instantiate database connection
        //write to db
    }
}