package com.example.jake_jay_p.datalabellerprototype;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetworkUtils
{

    // Log tag
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Will be bound together to make a single URI
    private static final String BASE_URL =  "http://10.20.20.101:5000/api/"; // Base URI
    private static final String GET_PATTERN =  "pattern";
    private static final String UNLABELLED =  "unlabelled"; // Base URI


    // ---- Method takes a string (query) and returns the JSON output from the API
    static String getPattern(String deviceID){

        // Local variables needed to setup the connection
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        // Local variables used to return the raw JSON response
        String JSONString = null;


        try{
            Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(GET_PATTERN)
                            .appendPath(UNLABELLED)
                            .appendQueryParameter("deviceID", deviceID)
                            .build();
            URL requestURL = new URL(builtURI.toString());

            Log.d(LOG_TAG, requestURL.toString());

            // ----Make the connection
            conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();


            InputStream inputStream = conn.getInputStream();
            if (inputStream == null) {
                // ---- Nothing to do since there was no response
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream)); // Buffer reader reads input stream

            StringBuffer buffer = new StringBuffer();   // Used to store the response
            String line;
            while (( line = reader.readLine()) != null) {
                /* Since it's JSON, adding a newline isn't necessary (it won't affect
                parsing) but it does make debugging a easier if you print out the
  c             completed buffer for debugging. */
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            JSONString = buffer.toString(); // The final JSON output is saved as a string

            // ---- Can return the full response from the server
            // Log.d(LOG_TAG, "\n"+ bookJSONString);
            return JSONString;

        }
        catch (Exception ex){
            Log.d(LOG_TAG, "Network connection failed");
            ex.printStackTrace();
            return null;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
