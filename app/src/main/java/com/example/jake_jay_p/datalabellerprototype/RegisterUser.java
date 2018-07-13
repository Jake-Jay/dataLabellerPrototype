package com.example.jake_jay_p.datalabellerprototype;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * <h>Register User</h>
 * <p>
 *     Registers a new user in the database so that they can label images. If they are not
 *     registered, none of their actions will be logged. If they are already registered, the DB is
 *     not changed.
 * </p>
 */
public class RegisterUser extends AsyncTask<String, Void, String> {

    // ---- Set the parameters to connect to the API of choice
    private static final String LOG_TAG = RegisterUser.class.getSimpleName();
    private static final String BASE_URL =  "http://10.20.20.101:5000/api/";    // Base URI
    private static final String LABEL_1 =  "register";                          // Register Device


    /**
     * Asynchronous task that connects to the API to register a user by passing their device IMEI to
     * the API.
     *
     * @param strings Should have only one element - contains the JSON data to be passed to the API
     * @return String Message received from the API.
     */
    @Override
    protected String doInBackground(String... strings) {

        String JsonResponse = null;     // For JSON response from server
        String JsonDATA = strings[0];   // JSON data to send to server

        Log.d(LOG_TAG, "Should be sending " + JsonDATA + " to the server.");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(LABEL_1)
                    .build();
            URL postURL = new URL( builtURI.toString() );
            Log.d(LOG_TAG, postURL.toString());
            urlConnection = (HttpURLConnection) postURL.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();

            Log.d(LOG_TAG, "Connection is successful");

            //---- Set the header and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            writer.close();

            Log.d(LOG_TAG, "Data sent to API");

            //---- Setup the input stream to receive the response
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if( inputStream == null){
                return null;
            }

            Log.d(LOG_TAG, "Response received from API");

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while (( line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            JsonResponse = buffer.toString(); // The final JSON output is saved as a string

            Log.d(LOG_TAG, JsonResponse);

            return JsonResponse;


        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Cannot create URL: ", e);
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
