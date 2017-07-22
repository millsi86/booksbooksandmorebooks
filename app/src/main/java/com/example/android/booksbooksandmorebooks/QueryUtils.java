package com.example.android.booksbooksandmorebooks;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom.mills-mock on 22/07/2017.
 */

public class QueryUtils {

    // Tag for the log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    // Constant keys
    private static final String JSON_TOTAL_ITEMS = "totalItems";
    private static final String JSON_ITEMS = "items";
    private static final String JSON_VOLUME_INFO = "volumeInfo";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SUBTITLE = "subtitle";
    private static final String JSON_AUTHORS = "authors";
    private static Context context;

    /**
     * Query the Google dataset and return a list of {@link Book} objects.
     *
     * @param requestUrl is the URL we are requesting data from
     * @return books
     */
    public static List<Book> fetchBookData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchBookData() called...");

        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        // Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        /*
          Extract relevant fields from the JSON response and create a list of {@link Book}s.
         */

        /*
          Return the list of {@link Book}s.
         */
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a lost of {@link Book} objects that has been build up from
     * parsing the given JSON response
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        //Create an empty ArrayList for the books
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Check if there are no books returned for the keywords and if so return noll
            int totalItems = baseJsonResponse.getInt(JSON_TOTAL_ITEMS);

            if (totalItems == 0) {
                return null;
            }

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray bookArray = baseJsonResponse.getJSONArray(JSON_ITEMS);

            // For each book in the earthquakeArray, create a book object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book JSONObject at position i
                JSONObject currentBook = bookArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all properties
                // for that earthquake.
                JSONObject volumeInfo = currentBook.getJSONObject(JSON_VOLUME_INFO);

                // Extract "title" under "volumeInfo" for title
                String title = "";
                if (volumeInfo.has(JSON_TITLE)) {
                    title = volumeInfo.getString(JSON_TITLE);
                } else
                    title = "No Title";

                // Extract "subtitle" under "volumeInfo" for subtitle
                String subtitle = "";
                if (volumeInfo.has(JSON_SUBTITLE)) {
                    subtitle = volumeInfo.getString(JSON_SUBTITLE);
                } else
                    subtitle = "No SubTitle";


                // Extract "authors" under "volumeInfo" for author
                String authors = "";
                if (volumeInfo.has(JSON_AUTHORS)) {
                    JSONArray authorsArray = volumeInfo.getJSONArray(JSON_AUTHORS);
                    authors = authorsArray.toString().replace("[", "").replace("]", "");
                } else
                    authors = "No Author";

                // Create the new book info
                Book book = new Book(title, subtitle, authors);

                // Add the book to the list of books
                books.add(book);
            }

        } catch (JSONException e) {
            /*
             * If an error is thrown when executing any of the above statements in the "try" block,
             * catch the exception here, so the app doesn't crash. Print a log message with the
             * message from the exception.
             */
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        return books;
    }
}
