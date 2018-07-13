package com.example.jake_jay_p.datalabellerprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    // ---- Global Variables
    // To connect to the UI
    ImageView pattern = null;
    TextView hiddenID = null;
    TextView label = null;
    TextView question = null;
    ImageButton yes = null;
    ImageButton no = null;
    Button next_button = null;
    // Other parameters
    String deviceID = null;     // Holds the IMEI
    String[] all_labels;        // All proposed labels returned for the image (specified by the image set)
    ArrayList<String> rejected_labels = new ArrayList<>();  // Will hold the rejected labels to make finding the correct one faster


    /**
     * On Create
     *
     * Sets up the UI; gets the device IMEI using the loading activity;
     * gets an image and label set to start off with using the IMEI when the
     * activity responds.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---- Reference Views in the layout
        pattern = findViewById(R.id.pattern);
        hiddenID = findViewById(R.id.hidden_imageID);
        label = findViewById(R.id.class_suggestion);
        question = findViewById(R.id.question);
        yes = findViewById(R.id.yes_symbol);
        no = findViewById(R.id.no_symbol);
        next_button = findViewById(R.id.next_button);



        // ---- Start the Loading Page Activity to get the user IMEI
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivityForResult(intent, 1);  // Result is sent to onActivityResult

    }


    /**
     * Waits for reply from the loading activity.
     * Reply contains the IMEI - used to set the deviceID
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                deviceID = data.getStringExtra(LoadingActivity.EXTRA_REPLY);
                retrieveImageLabelSet();
            }
        }
    }


    /**
     * Places a new image from the db and sets in the main image view for classification.
     * Should also get the proposed label for the image. The proposed label is randomly chosen from a set of labels
     * @param view
     */
    public void getNextImage(View view)
    {
        //---- Log button press
        Log.d(LOG_TAG, "Button pressed to get new data to label.");
        question.setVisibility(View.VISIBLE);
        label.setVisibility(View.VISIBLE);
        yes.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        next_button.setVisibility(View.INVISIBLE);

        retrieveImageLabelSet();
    }


    /**
     * The actual call to the asynchronous task that connects to the DB and retrieves
     * an image and the label set associated with the image. The entire set of labels is passed to
     * the variable all_labels which cna be iterated through for each image until the correct label
     * is found or the next image is retrieved.
     */
    public void retrieveImageLabelSet(){

        new GetUnlabelledImage(pattern, label, hiddenID,this, yes, no, question,
                new GetUnlabelledImage.AsyncResponse(){

                    @Override
                    public void processFinish(String[] output) {
                        all_labels = output;    // Get the labels from the asynchronous task
                    }
                }).execute(deviceID);
    }


    /**
     * When the YES button is pressed
     * Updates the db with a new label confirming what was suggested by the app.
     * @param view
     */
    public void proceedYes(View view)
    {
        // ---- Log button press
        Log.d(LOG_TAG, "YES! Button pressed to confirm proposed label.");

        // ---- Found the correct label. Clear the rejected labels for the next image
        rejected_labels.clear();

        question.setVisibility(View.INVISIBLE);
        label.setVisibility(View.INVISIBLE);
        yes.setVisibility(View.INVISIBLE);
        no.setVisibility(View.INVISIBLE);
        next_button.setVisibility(View.VISIBLE);

        // ---- Add a label into the db
        String chosenLabel = label.getText().toString();        // Get the random label suggested
        if(deviceID == null)
            deviceID = "990000862401822";   // This is a fall back in case there is a problem getting the IMEI
        else
            Log.d(LOG_TAG, "Device ID is: " + deviceID);

        JSONObject toPost = new JSONObject();                   // The data that is sent to the API

        try {
            toPost.put("label", chosenLabel);
            toPost.put("device_number", deviceID);
        } catch (JSONException e) {
            Log.d( LOG_TAG, "Error creating JSON object: " + e );
        }

        // ---- If there is data to post and the patternID is not 0 (zero is the default and means that the connection is not working)
        if (toPost.length() > 0 && !( hiddenID.getText().toString().equals("0")) ) {
            Log.d(LOG_TAG, "Try add a label to the image with ID: " + hiddenID.getText().toString());
            new AddLabel().execute( String.valueOf(toPost), hiddenID.getText().toString() );    // Add a label
        }
    }

    /**
     * When the NO button is clicked.
     * Updates the db
     * @param view NO button
     */
    public void proceedNo(View view)
    {
        // ---- Log button press
        Log.d(LOG_TAG, "NO! Button pressed to reject proposed label. Propose a new one");

        // ---- Suggest a new Label from the prospective list given
        rejected_labels.add( label.getText().toString() );      // Adds rejected label
        Log.d(LOG_TAG, "Size of array list should be increasing: " + rejected_labels.size() );
        getNewProposedLabel(all_labels, label.getText().toString() );

    }


    /**
     * Sets the label in the main layout to display a new layout when  the current one has been marked as incorrect
     * @param all_labels all proposed labels for the image set
     * @param currentLabel the label that the system has (randomly) suggested
     */
    public void getNewProposedLabel(String[] all_labels, String currentLabel){

        String newLabel = currentLabel;
        int index;

        if(rejected_labels.size() == all_labels.length){

            /*  If the user either missed the correct label by mistake or the correct label
            does not exist. Clear the rejected labels and get a new image and label set.
             */
            rejected_labels.clear();
            retrieveImageLabelSet();

            return;
        }


        // ---- Find a label for the current image that has not yet been rejected
        while( rejected_labels.contains(newLabel) ){
            index = GetUnlabelledImage.randInt(0, all_labels.length-1);
            newLabel = all_labels[index];
        }

        label.setText(newLabel);
    }

}
