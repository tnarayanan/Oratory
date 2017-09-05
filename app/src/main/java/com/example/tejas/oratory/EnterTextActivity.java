package com.example.tejas.oratory;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EnterTextActivity extends AppCompatActivity {

    TextView titleText;
    EditText editText;
    Button speechDoneButton;

    String speechTitle;
    String fullTextString;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_text);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.roundedlogo);
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.colorPrimaryDark));

        context = this;

        titleText = (TextView) findViewById(R.id.titleText);
        editText = (EditText) findViewById(R.id.editText);
        speechDoneButton = (Button) findViewById(R.id.speechDoneButton);

        speechDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTextString = editText.getText().toString();
                MainActivity.stringParagraphs = MainActivity.makeParagraphs(fullTextString);

                // Prompt user for speech title

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Speech Title");

                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        speechTitle = input.getText().toString();
                        try {
                            FileOutputStream outputStream = openFileOutput("speeches.txt", Context.MODE_APPEND);
                            /*outputStream.write("   \n".getBytes());
                            outputStream.write((speechTitle + "\n").getBytes());
                            for(String str : stringParagraphs) {
                                outputStream.write((str + "\n").getBytes());
                            }*/


                            InputStream inputStream = openFileInput("speeches.txt");

                            boolean speechTitleExists = false;
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String recieveString;
                            //System.out.println("GARBANZO");
                            while ((recieveString = bufferedReader.readLine()) != null) {
                                //System.out.println("GARBANZO");
                                if (recieveString.equals("   ")) {
                                    if (bufferedReader.readLine().equals(speechTitle)) {
                                        speechTitleExists = true;
                                        break;
                                    }
                                }
                            }
                            bufferedReader.close();

                            if (!speechTitleExists) {
                                //Toast.makeText(getApplicationContext(), "DOES NOT EXIST", Toast.LENGTH_LONG).show();
                                outputStream.write("   \n".getBytes());
                                outputStream.write((speechTitle + "\n").getBytes());
                                for(String str : MainActivity.stringParagraphs) {
                                    outputStream.write((str + "\n").getBytes());
                                }
                                outputStream.close();
                            } else {
                                //Toast.makeText(getApplicationContext(), "EXISTS", Toast.LENGTH_LONG).show();
                                new AlertDialog.Builder(context)
                                        .setTitle("Duplicate Title")
                                        .setMessage("Please enter a different title for the speech.")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                            outputStream.close();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(), Memorize.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });
    }
}
