package com.example.tejas.oratory;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.roundedlogo);
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));

        listView = (ListView) findViewById(R.id.listView);

        try {
            /*FileOutputStream outputStream = openFileOutput("speeches", Context.MODE_PRIVATE);
            String str = "   \n" +
                    "TITLE 1\n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "   \n" +
                    "TITLE 2\n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n" +
                    "\n" +
                    "speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech speech \n";

            outputStream.write(str.getBytes());*/

            ArrayAdapter<String> adapter = new ArrayAdapter<>(LoadActivity.this, android.R.layout.simple_list_item_1);

            /*BufferedReader br = new BufferedReader(new FileReader(new File(getFilesDir(), "speeches")));


            String line;
            while((line = br.readLine()) != null) {
                if (line.equals("   ")) {
                    adapter.add(br.readLine());
                }
            }*/

            InputStream inputStream = openFileInput("speeches.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String recieveString;
                while ((recieveString = bufferedReader.readLine()) != null) {
                    if (recieveString.equals("   ")) {
                        adapter.add(bufferedReader.readLine());
                    }
                }

                bufferedReader.close();

            }


            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println(position);
                    int i = -1;
                    MainActivity.stringParagraphs.clear();
                    try {
                        InputStream inputStream = openFileInput("speeches.txt");

                        if (inputStream != null) {
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String recieveString;
                            while ((recieveString = bufferedReader.readLine()) != null) {
                                if (recieveString.equals("   ")) {
                                    i++;
                                    if (position == i) {
                                        bufferedReader.readLine();
                                        while ((recieveString = bufferedReader.readLine()) != null && !(recieveString.equals("   "))) {
                                            MainActivity.stringParagraphs.add(recieveString);
                                        }
                                    }
                                }
                            }

                            bufferedReader.close();

                            System.out.println(MainActivity.stringParagraphs.toString());

                            Intent intent = new Intent(getApplicationContext(), Memorize.class);
                            startActivity(intent);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
