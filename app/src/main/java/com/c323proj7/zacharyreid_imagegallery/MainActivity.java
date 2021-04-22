package com.c323proj7.zacharyreid_imagegallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GalleryImageAdapter.AdapterListener {
    //API Key: 941a6f3e5d96f9b0cdc412c1b095cf66
    //Secret: 32771f614e7df84d
    private final String LINK_URL = "https://api.flickr.com/services/rest/?method=flickr.galleries.getPhotos&api_key=941a6f3e5d96f9b0cdc412c1b095cf66&gallery_id=66911286-72157647277042064&format=json&nojsoncallback=1";
    private RecyclerView recyclerView;
    public static GalleryArrayList galleryImages;
    private GalleryImageAdapter adapter;
    private String rawJSONData;
    private boolean galleryDownloaded = false;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //Log.i("DEBUG_MAIN", "PERMISSION REQUESTED");
        recyclerView = findViewById(R.id.recyclerView);
        imageView = findViewById(R.id.imageView);
        galleryImages = new GalleryArrayList();
        adapter = new GalleryImageAdapter(galleryImages, this);

        Log.i("DEBUG_MAIN", "1");
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        Log.i("DEBUG_MAIN", "2");


        Log.i("DEBUG_MAIN", "3");

        Log.i("DEBUG_MAIN", "4");
        //Log.i("DEBUG_MAIN", "LAYOUT SET");


        //adapter = new GalleryImageAdapter();
        //Log.i("DEBUG_MAIN", "ADAPTER CREATED");
        //recyclerView.setAdapter(adapter);
        //Log.i("DEBUG_MAIN", "ADAPTER SET");
        retrieveJSONData();


        /*while(!galleryDownloaded) {
            Log.i("DEBUG", "NOPE");
        }*/
        //new DownloadGalleryImagesTask().execute(galleryImages);


    }

    /*private class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ImageViewHolder> {
        private Context context;
        private ArrayList<GalleryImage> images;

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            public ImageViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.galleryImage);
            }
        }

        public GalleryImageAdapter(Context context) {
            images = galleryImages;
            this.context = context;
        }


        @NonNull
        @Override
        public GalleryImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView view = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_image_layout, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryImageAdapter.ImageViewHolder holder, int position) {
            if(!images.get(position).isDefaultMode()) {
                holder.imageView.setImageBitmap(images.get(position).getBitmap());
            } else {
                holder.imageView.setImageResource(images.get(position).getDefaultImage());
            }
        }

        @Override
        public int getItemCount() {
            return 13;
        }
    }*/

    private void retrieveJSONData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            new DownloadGalleryTask().execute(LINK_URL);
        } else { Log.i("DEBUG_MAIN", "NO NETWORK CONNECTION AVAILABLE."); }
    }

    private void parseJSONData(String rawJSONData) {
        try {
            JSONObject jsonRootObject = new JSONObject(rawJSONData);
            JSONArray jsonArray = jsonRootObject.getJSONObject("photos").getJSONArray("photo");
            for(int i = 0; i < jsonArray.length(); i++) {
                String farm = jsonArray.getJSONObject(i).optString("farm");
                String id = jsonArray.getJSONObject(i).optString("id");
                String server = jsonArray.getJSONObject(i).optString("server");
                String secret = jsonArray.getJSONObject(i).optString("secret");
                String title = jsonArray.getJSONObject(i).optString("title");
                //Log.i("DEBUG_PARSING", "farm: "+farm+"/id: "+id+"/server: "+server+"/secret: "+secret);
                galleryImages.add(new GalleryImage(farm, id, server, secret, title));
            }

            //Log.i("DEBUG_PARSING", galleryImages.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("DEBUG_MAIN", e.toString());
        }
        /*for(GalleryImage i : galleryImages) {
            Log.i("DEBUG_PARSING", i.getStringURL());
        }*/
        galleryDownloaded = true;
        new DownloadGalleryImagesTask().execute(galleryImages);
    }

    private class DownloadGalleryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            rawJSONData = downloadFromURL(urls[0]);
            return rawJSONData;
        }

        private String downloadFromURL(String url) {
            InputStream in = null;
            StringBuffer result = new StringBuffer();
            URL myURL = null;
            try {
                myURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();
                int response = connection.getResponseCode();
                in = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = "";
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.i("DEBUG_MAIN", "ON POST EXECUTE");
            boolean thread = Looper.myLooper() == Looper.getMainLooper();
            //Log.i("DEBUG_MAIN", "MAIN_THREAD? "+String.valueOf(thread));
            parseJSONData(rawJSONData);

        }
    }

    private class DownloadGalleryImagesTask extends AsyncTask<ArrayList<GalleryImage>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ArrayList<GalleryImage>... galleryImages) {
            ArrayList<GalleryImage> images = galleryImages[0];
            try {
                for(int i = 0; i < images.size(); i++) {
                    GalleryImage image = images.get(i);
                    //Log.i("DEBUG_DGIT", String.valueOf(image.isDefaultMode()));
                    if(!image.isDefaultMode()){
                        InputStream in = new java.net.URL(image.getStringURL()).openStream();
                        //image might not be a shallow copy so its bitmap may not get updated
                        image.setBitmap(BitmapFactory.decodeStream(in));
                        //Log.i("DEBUG_DGIT", image.getBitmap().toString());
                        //publishProgress();
                    } else {
                        //publishProgress();
                        //the default one using the default image
                    }
                    //adapter.notifyDataSetChanged();

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            /*for(GalleryImage i : galleryImages) {
                Log.i("DEBUG_PARSING", i.getBitmap().toString());
            }*/
            imageView.setImageResource(R.drawable.default_image);


        }
    }

    public void onAdapterSignal(int position) {
        Intent intent = new Intent(MainActivity.this, ZoomedActivity.class);
        intent.putExtra("POSITION", position);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == 100) {
            if(data.hasExtra("TO_REMOVE")) {
                String toRemove = data.getStringExtra("TO_REMOVE");
                adapter.remove(toRemove);
            }
        }
    }
}
