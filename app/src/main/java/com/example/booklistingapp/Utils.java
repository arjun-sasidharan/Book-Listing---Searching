package com.example.booklistingapp;

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
import java.util.Arrays;
import java.util.List;

public class Utils {
    /*
    TODO: data that want to take : title, authors, publisher, date, categories
     */
    //private constructor
    private Utils(){
    }

    private static final String LOG_TAG = Utils.class.getSimpleName();

    //fetchBookData
    public static List<Book> fetchBookData(String requestUrl){
        Log.v(LOG_TAG, "fetchBookDate method is called");
        URL url = createUrl(requestUrl);

        //performing http request to get JSON data
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<Book> bookList = extractBookDataFromJson(jsonResponse);

        return bookList;

    }

    //create URL object from the string url
    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        Log.v(LOG_TAG, "Making http request");
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
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // to convert input stream bytes to string using buffer reader
    private static String readFromStream(InputStream inputStream) throws IOException{
        Log.v(LOG_TAG, "Converting input stream bytes to string");
        StringBuffer output = new StringBuffer();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // method to extract book data from json and store it in a list
    private static List<Book> extractBookDataFromJson(String bookJSON){
        Log.v(LOG_TAG, "Extracting book data from json and creating array list");

        if (TextUtils.isEmpty(bookJSON)){
            return null;
        }
        List<Book> books = new ArrayList<Book>();

        try{

            //creating json object to track the root
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // finding the json array "items"
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            //looping through each array items and add details in to the list
            for (int i = 0; i < booksArray.length(); i++) {
                Log.v(LOG_TAG, "Looping through the items array, current loop: " + i);
                //get ith object in the array
                JSONObject currentBook = booksArray.getJSONObject(i);

                //get volumeInfo object of current book

                JSONObject currentBookInfo = currentBook.getJSONObject("volumeInfo");

                //creating local variables for data
                String title, publisher, publishedDate, previewLink = null;
                String[] authors;

                try {
                    //get title
                    title = currentBookInfo.getString("title");
                    Log.v(LOG_TAG, "title: " + title);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Problem parsing the title from results, so adding Not available", e);
                    title = "Not available";
                }

                try {
                    //get authors, but its an array
                    JSONArray authorsArray = currentBookInfo.getJSONArray("authors");
                    //Creating a string array to store authors,
                    authors = new String[authorsArray.length()];
                    //loop through the author array
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authors[j] = authorsArray.getString(j);
                    }
                    Log.v(LOG_TAG, "authors: " + Arrays.toString(authors));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Problem parsing the publisher date from results, so adding Not available", e);
                    authors = new String[]{"Not available"};
                }

                    try {
                        //get publisher of current book
                        publisher = currentBookInfo.getString("publisher");
                        Log.v(LOG_TAG, "publisher: " + publisher);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Problem parsing the publisher date from results, so adding Not available", e);
                        publisher = "Not available";
                    }
                    /**
                     * this individual try and catch block helps the app to show result, even if some of data is missing
                     */

                    try {
                        //get published date
                        publishedDate = currentBookInfo.getString("publishedDate");
                        Log.v(LOG_TAG, "published date: " + publishedDate);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Problem parsing the publisher date from results, so adding Not available", e);
                        publishedDate = "Not available";
                    }

                    //get preview link
                    try{
                        previewLink = currentBookInfo.getString("previewLink");
                    }catch (JSONException e){
                        Log.e(LOG_TAG, "Problem parsing the preview link date from results, so adding Not available", e);
                        previewLink = "Not available";
                    }

                    //adding data into array list
                    books.add(new Book(title, authors, publisher, publishedDate, previewLink));
                }


        }catch (JSONException e){
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
        }
        //return the list of books
        return books;
        //if not returned in the try block, which means, error in parsing
       // return null;
    }

}
