package com.example.jake_jay_p.datalabellerprototype;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddLabel extends AsyncTask<String, Void, String> {

    //    POST -d '{"device_number":"990000862401821", "label":"hansa"}'
    //    http://localhost:5000/api/pattern/5/label
    private static final String LOG_TAG = AddLabel.class.getSimpleName();
    private static final String BASE_URL =  "http://10.20.20.101:5000/api/"; // Base URI
    private static final String LABEL_1 =  "pattern";
    private static final String LABEL_2 =  "label";


    @Override
    protected String doInBackground(String... strings) {

        String JsonResponse = null;     // For JSON response from server
        String JsonDATA = strings[0];   // JSON data to send to server
        String patternID = strings[1];  // Needed to label specific image


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(LABEL_1)
                    .appendPath(patternID)
                    .appendPath(LABEL_2)
                    .build();
            URL postURL = new URL(builtURI.toString());
            Log.d(LOG_TAG, postURL.toString());
            urlConnection = (HttpURLConnection) postURL.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();

            Log.d(LOG_TAG, "Should see this message if connection is successful 1");


            //---- Set the header and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            writer.close();

            Log.d(LOG_TAG, "Should see this message if connection is successful 2");

            //---- Setup the input stream to receive the response
            InputStream inputStream = urlConnection.getInputStream();
            Log.d(LOG_TAG, "Should see this message if connection is successful 3");

            if( inputStream == null){
                return null;
            }

            StringBuilder buffer = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while (( line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            Log.d(LOG_TAG, "Should see this message if connection is successful 4");

            JsonResponse = buffer.toString(); // The final JSON output is saved as a string

            Log.d(LOG_TAG, JsonResponse);

            return JsonResponse;


        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Cannot create URL: " + e);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error opening connection: ", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }
}







