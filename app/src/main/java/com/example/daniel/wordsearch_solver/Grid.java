package com.example.daniel.wordsearch_solver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ScaleXSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by daniel on 3/20/16.
 */
public class Grid {

    private HashSet<String> dictionary;
    private ArrayList<String> grid;
    int numRows;
    int numCols;
    Context context;
    Activity activity;

    TextView tv;

    public Grid(Activity activity)
    {
        this.activity = activity;

        try {


            init(activity.getResources().openRawResource(R.raw.search));
        }catch(IOException e)
        {
            e.printStackTrace();
            // throw error message to user
            Toast.makeText(activity, "Error in loading inputStream", Toast.LENGTH_LONG).show();

        }
        catch(InvalidGridException e)
        {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to load grid from image", Toast.LENGTH_LONG).show();
        }
    }

    private void init(InputStream fileName) throws IOException, InvalidGridException
    {
        Scanner in = new Scanner(fileName);
        grid = new ArrayList<>();

        String currRow = in.nextLine();
        int rowCount = currRow.length();

        //Log.e("INIT", currRow);
        grid.add(currRow);

        while (in.hasNextLine()) {
            currRow = in.nextLine();
            if (currRow.length() != rowCount)
                throw new InvalidGridException("Invalid Grid");

            Log.e("INIT", currRow);
            grid.add(currRow);

        }




    }

    public void searchGrid(ArrayList<String> grid, HashSet<String> dictionary)
    {
        this.dictionary = dictionary;
        numRows = grid.size();
        numCols = grid.get(0).length();

        for (int i = 0; i < numRows; i++) // row
        {
            for (int j = 0; j < numCols; j++) // col
            {
                StringBuilder s = new StringBuilder();
                s.append(grid.get(i).charAt(j));

                traverseGrid("left", j-1, i, numRows, numCols, s.toString(), grid);
                traverseGrid("right", j+1, i, numRows, numCols, s.toString(), grid);
                traverseGrid("up", j, i+1, numRows, numCols, s.toString(), grid);
                traverseGrid("down", j, i-1, numRows, numCols, s.toString(), grid);
                traverseGrid("ul", j-1, i+1, numRows, numCols, s.toString(), grid);
                traverseGrid("ur", j+1, i+1, numRows, numCols, s.toString(), grid);
                traverseGrid("dl", j-1, i-1, numRows, numCols, s.toString(), grid);
                traverseGrid("dr", j+1, i-1, numRows, numCols, s.toString(), grid);


            } // endfor
        } // endfor
    } // end function

    private String longestString;
    private void traverseGrid(String direction, int x, int y, int rowSize, int colSize, String currentStr, ArrayList<String> grid)
    {
        if (x < 0 || x >= colSize || y < 0 || y >= rowSize) {
            if (longestString != null)
                System.out.printf("Word %s found\n", longestString);

            longestString = null;
            return;
        }

        currentStr = currentStr + grid.get(y).charAt(x);
        if (dictionary.contains(currentStr.toLowerCase()))
            longestString = currentStr;

        switch (direction)
        {
            case ("left"):
                traverseGrid(direction, x-1, y, rowSize, colSize, currentStr, grid);
                break;
            case ("right"):
                traverseGrid(direction, x+1, y, rowSize, colSize, currentStr, grid);
                break;
            case ("up"):
                traverseGrid(direction, x, y+1, rowSize, colSize, currentStr, grid);
                break;
            case ("down"):
                traverseGrid(direction, x, y-1, rowSize, colSize, currentStr, grid);
                break;
            case ("ul"):
                traverseGrid(direction, x-1, y+1, rowSize, colSize, currentStr, grid);
                break;
            case ("ur"):
                traverseGrid(direction, x+1, y+1, rowSize, colSize, currentStr, grid);
                break;
            case ("dl"):
                traverseGrid(direction, x-1, y-1, rowSize, colSize, currentStr, grid);
                break;
            case ("dr"):
                traverseGrid(direction, x+1, y-1, rowSize, colSize, currentStr, grid);
                break;
        }
    }

    public void printGrid()
    {
        TextView textBox = (TextView) activity.findViewById(R.id.editText);
        MyView myView = new MyView(activity);


        RelativeLayout parent = (RelativeLayout) activity.findViewById(R.id.myLayout);
        tv = (TextView) activity.findViewById(R.id.editText);

        myView.setMaxHeight(tv.getHeight());

        parent.addView(myView);

        if (grid != null && 1==0)
        {
            for (int i = 0; i < grid.size(); i++) {
                String currString = grid.get(i);
                String newString = currString.charAt(0) + "";
                for (int j = 1; j < currString.length(); j++)
                {
                    newString += "" + currString.charAt(j);

                }

                float textSize = getTextSize(newString);
                float text_view_size = textBox.getWidth();

                Log.e("PRINT", "Text sizes: " + textSize + " " + text_view_size);

                if (textSize > text_view_size)
                {
                    Log.e("PRINT", "Text has greater size than text view: " + textSize + " " + text_view_size);
                }

                textBox.append(newString + "\n");

            }
        }
    }

    private float getTextSize(String your_text){
        Paint p = new Paint();
        //Calculate the text size in pixel
        return p.measureText(your_text);
    }




    class MyView extends View
    {
        //Paint p = new Paint();
        TextPaint p = new TextPaint();
        float maxHeight;
        float textHeight;


        public MyView(Context context) {
            super(context);

        }

        public void setMaxHeight(float height) {
            maxHeight = height;
        }

        private float getGridHeight(TextPaint paint)
        {
            numRows = grid.size();
            textHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
            return textHeight * numRows;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int textSize = 20;
            p.setTextSize(textSize);
            p.setTypeface(Typeface.MONOSPACE);

            //p.setLetterSpacing(2);
            p.setTextAlign(Paint.Align.CENTER);


            String text = "";

            for(int i = 0; i < grid.get(0).length(); i++)
            {
                text += grid.get(0).charAt(i) + " ";
            }

            //text = getSpacedString(text).toString();


            if (p.measureText(text) > canvas.getWidth() - 20)
            {
                do {
                    p.setTextSize(--textSize);
                }while ((p.measureText(text) > canvas.getWidth() - 20) || getGridHeight(p) > maxHeight);
            }
            else
            {
                do {
                    p.setTextSize(++textSize);
                    Log.e("HEIGHT", "" + getGridHeight(p) + ": " + maxHeight);
                }while (p.measureText(text) < canvas.getWidth() - 20 && getGridHeight(p) < maxHeight);

                p.setTextSize(--textSize);
            }

            canvas.drawText(text, canvas.getWidth()/2, 40, p);

            for (int i = 1; i < grid.size(); i++)
            {
                text = "";
                for(int j = 0; j < grid.get(i).length(); j++)
                {
                    text += grid.get(i).charAt(j) + " ";
                }


                canvas.drawText(text, canvas.getWidth()/2, 40 + textHeight*i, p);

            }

        } // end for



    } // end function

    public SpannableString getSpacedString(String text)
    {
        SpannableString finalText = new SpannableString(text);
        if(text.length() > 1) {
            for (int k = 1; k < text.length(); k += 2) {
                finalText.setSpan(new ScaleXSpan((0.2f + 1) / 10), k, k + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return finalText;
    }


}



class InvalidGridException extends Exception
{
    public InvalidGridException(String message)
    {
        super(message);
    }
}
