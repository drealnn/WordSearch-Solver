package com.example.daniel.wordsearch_solver;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    Grid myGrid;
    HashSet<String> dictionary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        try {
            loadDict(this.getResources().openRawResource(R.raw.english));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in loading dictionary", Toast.LENGTH_LONG).show();
        }
        */
    }

    public void loadGrid(View view) {
        myGrid = new Grid(this);
        myGrid.printGrid();
    }

    private void loadDict(InputStream fileName) throws IOException
    {

        new LoadDictTask().execute(fileName);
    }

    private class LoadDictTask extends AsyncTask<InputStream, Integer, Void>
    {

        private ProgressDialog progress;
        @Override
        protected Void doInBackground(InputStream... params)  {

            dictionary = new HashSet<>();
            BufferedReader in = new BufferedReader(new InputStreamReader(params[0]));

            String word;
            char currentChar = 'a';

            try {
                while ((word = in.readLine()) != null)
                {
                    //word = in.nextLine();
                    //Log.e("DICT", word);
                    if (word.length() > 2)
                        dictionary.add(word.toLowerCase());

                    if (!word.equals("") && currentChar != word.toLowerCase().charAt(0)) {
                        currentChar = word.toLowerCase().charAt(0);
                        int prog = (int) ((((float) currentChar - 96) / 26) * 100);
                        //Log.e("DICT", (int) ((( (float) word.toLowerCase().charAt(0) - 96)/26) *100) + "");
                        //Log.e("DICT" ,prog + "");
                        publishProgress(prog);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.e("DICT", values[0] + "");
            progress.setProgress(values[0]);

        }

        @Override
        protected void onPreExecute() {
            progress=new ProgressDialog(MainActivity.this);
            progress.setMessage("Loading Dictionary");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            //progress.setIndeterminate(true);
            progress.setProgress(10);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
        }
    }
}
