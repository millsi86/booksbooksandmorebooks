package com.example.android.booksbooksandmorebooks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    // Tag for the log messages
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Declare start of the url as a constant
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    // Loader unique ID
    private static final int BOOK_LOADER_ID = 1;
    //Adapter for the list of books
    private BookAdapter Adapter;
    // User keywords for searching the API
    private String userKeywords;
    // TextView for an empty book list
    private TextView EmptyBooksList;
    // Progress graphic view when connecting / downloading from the internet
    private View loadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Status: onCreate Started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get id's of items in the main activity
        ListView booksListView = (ListView) findViewById(R.id.booksList);
        final EditText Keywords = (EditText) findViewById(R.id.keywords);
        final Button search = (Button) findViewById(R.id.search);

        EmptyBooksList = (TextView) findViewById(R.id.emptyView);
        booksListView.setEmptyView(EmptyBooksList);
        EmptyBooksList.setText(R.string.InitialEmptyText);

        loadingIcon = findViewById(R.id.loading);
        loadingIcon.setVisibility(View.GONE);

        // Create a new adapter that takes an empty list of books as input
        Adapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(Adapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        /*
         * Initialise the loader, then pass in the int ID constant defined above and pass in null
         * for the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
         * because this activity implements the LoaderCallbacks interface.
         */

        loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);

        search.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                String searchKeywords = Keywords.getText().toString();
                userKeywords = searchKeywords.replace(" ", "+");

                // Modify state of UI views
                EmptyBooksList.setVisibility(View.GONE);
                loadingIcon.setVisibility(View.VISIBLE);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the current active default data network
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                // If there is a network connection, fetch the data
                if (networkInfo != null && networkInfo.isConnected()) {

                    // Restart the loader
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                } else {
                    // Error getting network connection, adjust UI views to notify User
                    loadingIcon.setVisibility(View.GONE);
                    Adapter.clear();
                    EmptyBooksList.setText(R.string.NoNetwork);
                }
            }
        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        return new BookLoader(this, BASE_URL, userKeywords);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> listOfBooks) {

        // Hide progress spinner because the data has been loaded
        loadingIcon.setVisibility(View.GONE);

        // clear the adapter of previous books data
        Adapter.clear();

        /*
          If there is a valid list of {@link listOfBooks}, then add them to the adapter's data set.
          this will trigger the ListView to update.
         */
        if (listOfBooks != null && !listOfBooks.isEmpty()) {
            Adapter.addAll(listOfBooks);
        } else if (userKeywords != null)
            EmptyBooksList.setText(R.string.noBooks);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data
        Adapter.clear();
    }
}
