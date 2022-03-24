package com.example.booklistingapp;

import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Text view that is displayed when the list is empty
     * */
    private static TextView mEmptyStateTextView;
    private static ProgressBar mLoadingBar;
//LOG TAG
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String BOOK_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG,"Search button clicked");
                //we have to read the search query in the search box
                EditText queryTextView = (EditText) findViewById(R.id.query_textview);
                String query = queryTextView.getText().toString();
                if (query.length()<1){
                    Log.v(LOG_TAG,"Empty query");
                    //here the query is empty, so do not call the http request
                    Toast.makeText(MainActivity.this, "Enter a book name to search", Toast.LENGTH_SHORT).show();
                }

                else{
                    Log.v(LOG_TAG,"Valid query");
                    if (isNetworkAvailable()){
                        //showing the progress bar
                        mLoadingBar.setVisibility(View.VISIBLE);
                        mEmptyStateTextView.setVisibility(View.GONE);

                        //call async task
                        //adding query to the url
                        String queryUrl = "" + BOOK_URL + query;
                        Log.v(LOG_TAG,"url is: "+ queryUrl);

                        //calling background task
                        BookAsyncTask backgroundTask = new BookAsyncTask();
                        backgroundTask.execute(queryUrl);
                    }
                    else{
                        Log.v(LOG_TAG,"No internet connection");
                        //here the query is empty, so do not call the http request
                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //making reference to the Empty state text view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        //making reference to the progress bar view
        mLoadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        //hiding by default
        mLoadingBar.setVisibility(View.INVISIBLE);

        ListView bookListView = (ListView) findViewById(R.id.list);

        //here empty list is added in the adapter initially
        adapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(adapter);
        bookListView.setEmptyView(mEmptyStateTextView);


        //on click listener to open preview link when a list item clicked
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = adapter.getItem(position);
                Uri previewUri = Uri.parse(currentBook.getPreviewLink());
                Intent weblinkIntent = new Intent(Intent.ACTION_VIEW, previewUri);
                startActivity(weblinkIntent);
            }
        });

    }

    private static class BookAsyncTask extends AsyncTask<String, Void, List<Book>>{

        @Override
        protected List<Book> doInBackground(String... url) {
            if (url.length == 0 || url[0] == null) {
                return null;
            }
            return  Utils.fetchBookData(url[0]);
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            mEmptyStateTextView.setText(R.string.no_books_found);
            //hiding the visibility of the progress bar
            mLoadingBar.setVisibility(View.INVISIBLE);

            //if list is null, do not update
            adapter.clear();
            if (books != null && !books.isEmpty()) {
                adapter.addAll(books);
            }
        }


    }
    /**
     * Method to check availability of internet connection
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
