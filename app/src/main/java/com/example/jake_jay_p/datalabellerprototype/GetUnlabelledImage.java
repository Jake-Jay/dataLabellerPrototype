package com.example.jake_jay_p.datalabellerprototype;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Random;


public class GetUnlabelledImage extends AsyncTask<String, Void, String>
{

//    https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a#
    public interface AsyncResponse {
        void processFinish(String[] output);
    }


    private static final String LOG_TAG = GetUnlabelledImage.class.getSimpleName();

    // ---- Needs access to certain views
    private ImageView pattern = null;
    private TextView label = null;
    private TextView hiddenID = null;
    private TextView question = null;
    ImageButton yes = null;
    ImageButton no = null;
    private Context context;            // Pass the context from the main activity to this class
    private String[] allLabels;

    public AsyncResponse delegate = null;


    public GetUnlabelledImage(ImageView p, TextView tv, TextView id,
                              Context activity_context, ImageButton yes, ImageButton no, TextView q,
                              AsyncResponse delegate) {
        pattern = p;
        label = tv;
        hiddenID = id;
        context= activity_context;
        this.delegate = delegate;
        this.yes = yes;
        this.no = no;
        this.question = q;

    }

    /**
     * Connects to the DB and gets a JSON output which contains the new pattern, potential labels,
     * data type, patternID
     * @param strings
     * @return
     */
    @Override
    protected String doInBackground(String... strings)
    {
        // ---- Make a call to the DB using the NetworkUtils helper class
        //      Async task then returns a string to be processed in the postExecute
        return NetworkUtils.getPattern( strings[0] );
    }

    /**
     *
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s == null){
            dbAlert();
            Log.d(LOG_TAG, "Nothing returned from DB");
            return;
        }

        // ---- Parse the JSON response here - recall that the string passed to onPostExecute
        //      comes from the async task
        try {
            // ---- Create a JSON object from the output of the async process and extract output
            JSONObject jsonObject = new JSONObject(s);
            JSONArray arr = jsonObject.getJSONArray("labels");      // All labels in arr
            String pattern_string = jsonObject.getString("data");   // BLOB image data
            String image_ID = jsonObject.getString("pattern_id");   // Pattern ID

            allLabels = new String[arr.length()];

            for(int i = 0; i<arr.length(); i++){
                String label = arr.getString(i);
                allLabels[i] = label;
                Log.d(LOG_TAG, label);
            }

            // ---- Try to set the array of labels on the UI thread
            delegate.processFinish(allLabels);

            // ---- Save ImageID in text view
            hiddenID.setText(image_ID);

            // ---- Find a random label from the list and set TextView
            int n = randInt(0, arr.length()-1);
            Log.d(LOG_TAG, "Random label: " + allLabels[n]);
            label.setText(allLabels[n]);


            // ---- Decode image and change Image view
            byte[] decodedString = Base64.decode(pattern_string, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            pattern.setImageBitmap(decodedByte);

        }catch(JSONException e){
            Log.d(LOG_TAG, "No result found");
            label.setText("Nothing left to label");
            yes.setVisibility(View.INVISIBLE);
            no.setVisibility(View.INVISIBLE);
            question.setVisibility(View.INVISIBLE);
            pattern.setImageResource(R.drawable.cheers);


        }//end try-catch
    }//end postExecute


    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }


    /**
     * Create an alert to show that there is no network connection
     */
    public void dbAlert(){
        AlertDialog.Builder myAlertBuilder = new
                AlertDialog.Builder(context);


        myAlertBuilder.setTitle("Cannot Connect to Database");
        myAlertBuilder.setMessage("Click OK once server is running, or Cancel to exit:");

        myAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User clicked OK button.
//                Toast.makeText(getApplicationContext(), "Pressed OK",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        myAlertBuilder.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog.
//                        Toast.makeText(getApplicationContext(), "Pressed Cancel",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

        myAlertBuilder.show();

    }
}//end class
