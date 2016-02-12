package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;
import it.jaschke.alexandria.utility.Utility;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";



    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);
        checkNetworkConnection();
        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }

                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                scanIntegrator.initiateScan();
            }

            public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    Toast toast = Toast.makeText(getActivity(),
                            "[" + scanContent + "]", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getActivity(),
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ean.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                ean.setText("");
            }
        });

        if(savedInstanceState !=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }



    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            Toast toast = Toast.makeText(getActivity(),
                    "["+scanContent+"]", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
        ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        ImageView imgBookCover = (ImageView)rootView.findViewById(R.id.bookCover);
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            if (Utility.isNetworkAvailable(getActivity())) {
                new DownloadImage(imgBookCover).execute(imgUrl);
                imgBookCover.setVisibility(View.VISIBLE);
            } else {
                imgBookCover.setImageResource(R.drawable.noimage);
                imgBookCover.setVisibility(View.VISIBLE);
            }
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
        @BookService.ConnectivityStatus int status = Utility.getConnectionStatus(getActivity());
        if(status==BookService.STATUS_INVALID_ISBN){
            TextView txtNoNetwork =  (TextView)rootView.findViewById(R.id.msgNoNetwork);
            txtNoNetwork.setText("");
            txtNoNetwork.setVisibility(View.GONE);
            Log.i(TAG, "clearFields GONE");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    public boolean displayMessage(){

        TextView txtNoNetwork =  (TextView)rootView.findViewById(R.id.msgNoNetwork);

        @BookService.ConnectivityStatus int status = Utility.getConnectionStatus(getActivity());
        int message = R.string.msg_ok;
        switch (status) {
            case BookService.STATUS_OK:
                message = R.string.msg_ok;
                break;
            case BookService.STATUS_ERROR_SERVER_DOWN:
                message = R.string.msg_error_server_down;
                break;
            case BookService.STATUS_ERROR_SERVER_INVALID:
                message = R.string.msg_error_server_invalid;
                break;
            case BookService.STATUS_ERROR_CONNECTION_UNKNOWN:
                message = R.string.msg_error_unknown;
                break;
            case BookService.STATUS_INVALID_ISBN:
                message = R.string.msg_invalid_isbn;
                break;
            default:
                if (!Utility.isNetworkAvailable(getActivity())) {
                    message = R.string.msg_no_network;
                }
                break;
        }
        if (message == R.string.msg_ok) {
            Log.i(TAG, "displayMessage _ok");
            txtNoNetwork.setVisibility(View.GONE);
            return false;
        }

        Log.i(TAG, "displayMessage "+message);
         txtNoNetwork.setText(message);
         txtNoNetwork.setVisibility(View.VISIBLE);
        return true;
    }

    private void checkNetworkConnection(){
        TextView txtNoNetwork =  (TextView)rootView.findViewById(R.id.msgNoNetwork);
        if (!Utility.isNetworkAvailable(getActivity())) {
            Log.i(TAG, "checkNetworkConnection msgNoNetwork");
            txtNoNetwork.setText(R.string.msg_no_network);
            txtNoNetwork.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "checkNetworkConnection GONE");
            txtNoNetwork.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences objSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        objSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences objSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.i(TAG, "onPause [" + objSharedPreferences +"]");
        Log.i(TAG, "onPause -[" + objSharedPreferences.getAll()+"]");
        objSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "onSharedPreferenceChanged ["+sharedPreferences+"]");
        if(key.equals(getString(R.string.pref_connectivity_status_key))){
            displayMessage();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences objSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        objSharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences objSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        objSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
