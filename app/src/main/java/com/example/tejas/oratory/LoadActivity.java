package com.example.tejas.oratory;

import android.support.v7.app.ActionBar;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadActivity extends AppCompatActivity {

    ListView listView;
    MenuItem editButton;
    MenuItem deleteButton;

    ArrayAdapter<String> adapter;

    ActionBar actionBar;

    int itemSelected = -1;
    Window window;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.roundedlogo);
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));

        window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.colorPrimaryDark));

        actionBar = getSupportActionBar();
        setActionBarColor(actionBar, R.color.colorPrimary);

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

            adapter = new ArrayAdapter<>(LoadActivity.this, android.R.layout.simple_list_item_1);

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

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    // show menu buttons

                    buttonsVisible(true);
                    setActionBarColor(actionBar, R.color.mistDark);

                    // Do animation of flip image

                    // Set variable to which item is selected

                    itemSelected = position;


                    return true;
                }
            });



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (itemSelected != -1) {
                        itemSelected = -1;

                        // animation back

                        // hide menu buttons

                        buttonsVisible(false);
                        setActionBarColor(actionBar, R.color.colorPrimary);
                    } else {

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
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.loadmenu, menu);

        editButton = menu.findItem(R.id.editButton);
        deleteButton = menu.findItem(R.id.deleteButton);

        buttonsVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.editButton:

                break;
            case R.id.deleteButton:
                try {
                    deleteSpeech(adapter.getItem(itemSelected));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buttonsVisible(boolean visible) {
        editButton.setVisible(visible);
        deleteButton.setVisible(visible);
    }

    private void setActionBarColor(ActionBar bar, int r) {
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), r)));
        bar.setTitle(Html.fromHtml("<font color='#ffffff'>Load a Speech</font>"));
        if (r == R.color.colorPrimary) window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.colorPrimaryDark));
        else if (r == R.color.mistDark) window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.stone));
    }

    private void deleteSpeech(String s) throws IOException {

        File newSpeeches = new File("newSpeeches.txt");
        newSpeeches.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(newSpeeches, false);
        InputStream inputStream = openFileInput("speeches.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String currLine;
        while((currLine = br.readLine()) != null) {
            if (!currLine.equals(s)) {
                outputStream.write((currLine + "\n").getBytes());
            } else {
                while ((currLine = br.readLine()) != null && !currLine.equals("   ")) {}
            }
        }

        File oldSpeeches = new File("speeches.txt");
        oldSpeeches.delete();
        newSpeeches.renameTo(oldSpeeches);
    }
}
