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

/**
 * <h>Network Utility</h>
 *
 * <p>
 *     A utility class that can be generalised to connect to any API and pass a string parameter
 *     to it. The response is then captured in a JSON response and returned to the asynchronous task
 *     which called the object instance.
 * </p>
 */
public class NetworkUtils
{

    // ---- Log tag
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // ---- Will be bound together into a single URI (change to connect to another API)
    private static final String BASE_URL =  "http://10.20.20.101:5000/api/"; // Base URI
    private static final String GET_PATTERN =  "pattern";
    private static final String UNLABELLED =  "unlabelled"; // Base URI


    // ---- Method takes a string (query) and returns the JSON output from the API

    /**
     * Method that can be called by any asynchronous task in this package. It connects to the API,
     * retrieves, and then returns the JSON response.
     *
     * @param deviceID Device ID of registered Device (IMEI)
     * @return JSON response from the web server.
     */
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

            // ---- Check that the URL has been built correctly.
            Log.d(LOG_TAG, requestURL.toString());

            // ---- Make the connection
            conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();


            InputStream inputStream = conn.getInputStream();
            if (inputStream == null) {
                // ---- Nothing to do since there was no response
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));  // Reads input stream
            StringBuffer buffer = new StringBuffer();                         // Store the response
            String line;
            while (( line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            JSONString = buffer.toString(); // The final JSON output is saved as a string

            // ---- Can print the full response from the server to the logcat
            // Log.d(LOG_TAG, "\n"+ bookJSONString);
            return JSONString;

        }
        catch (Exception ex){
            Log.d(LOG_TAG, "Network connection failed", ex);
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
