package com.c323proj7.zacharyreid_imagegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ZoomedActivity extends AppCompatActivity implements SaveImageDialog.SaveDialogListener, ShareImageDialog.ShareDialogListener {
    private ImageView zoomedImage;
    private ImageButton previousButton, nextButton;
    private Button shareButton, deleteButton, saveButton;
    private GalleryArrayList images;
    private int currentPhoto;
    private String toRemove;
    private ArrayList<Integer> toRemoveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed);
        images = MainActivity.galleryImages;
        Intent intent = getIntent();
        int position = intent.getIntExtra("POSITION",0);
        Log.i("DEBUG_ZOOMED", String.valueOf(position));
        currentPhoto = position;
        toRemove = "";
        toRemoveList = new ArrayList<>();

        zoomedImage = findViewById(R.id.zoomedImage);
        previousButton = findViewById(R.id.leftArrowButton);
        nextButton = findViewById(R.id.rightArrowButton);
        shareButton = findViewById(R.id.shareButton);
        deleteButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);

        zoomedImage.setImageBitmap(images.get(position).getBitmap());
        zoomedImage.setScaleX(Float.valueOf("1.5"));
        zoomedImage.setScaleY(Float.valueOf("1.5"));

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("DEBUG_ZOOMED_PREV_CLICK", currentPhoto+"");
                /*if(currentPhoto!=0) {
                    currentPhoto -= 1;
                    zoomedImage.setImageBitmap(images.get(currentPhoto).getBitmap());
                }*/
                previousImage();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("DEBUG_ZOOMED_NEXT_CLICK", currentPhoto+"");
                nextImage();
                /*if(currentPhoto!=images.size()-1) {
                    currentPhoto += 1;
                    zoomedImage.setImageBitmap(images.get(currentPhoto).getBitmap());
                }*/
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShareDialog();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(images.size() == 1) {
                    //toRemoveList.add(currentPhoto);
                    //images.remove(currentPhoto);
                    toRemove+=currentPhoto+"/";
                    images.remove(currentPhoto);
                    allSet(toRemove);
                } else if(currentPhoto == images.size()-1) {
                    //toRemoveList.add(currentPhoto);
                    toRemove += currentPhoto+"/";
                    previousImage();
                    images.remove(currentPhoto+1);


                } else {
                    //toRemoveList.add(currentPhoto);
                    toRemove += currentPhoto+"/";
                    nextImage();
                    images.remove(currentPhoto-1);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveDialog();
            }
        });
    }

    private void previousImage() {
        /*if(currentPhoto != 0) {
            int temp = currentPhoto;
            while(currentPhoto != 0 && toRemoveList.contains(currentPhoto-1)) {
                currentPhoto-=1;
                Log.i("DEBUG_ZOOMED_PREV_WHILE", currentPhoto+"/"+temp);
            }
            if(currentPhoto == 0 && !toRemoveList.contains(currentPhoto-1)) {
                zoomedImage.setImageBitmap(images.get(currentPhoto).getBitmap());
            } else {
                currentPhoto = temp;
                Log.i("DEBUG_ZOOMED_PREV_ELSE", currentPhoto+"/"+temp);
            }
        }*/
        if(currentPhoto!=0) {
            currentPhoto -= 1;
            zoomedImage.setImageBitmap(images.get(currentPhoto).getBitmap());
        }
    }

    private void nextImage() {
        /*if(currentPhoto != images.size()-1) {
            int temp = currentPhoto;
            while(currentPhoto != images.size()-1 && toRemoveList.contains(currentPhoto+1)) {
                currentPhoto+=1;
                Log.i("DEBUG_ZOOMED_NEXT_WHILE", currentPhoto+"/"+temp);
            }
            if(currentPhoto == images.size()-1 && !toRemoveList.contains(currentPhoto+1)) {
                zoomedImage.setImageBitmap(images.get(currentPhoto).getBitmap());
            } else {
                currentPhoto = temp;
                Log.i("DEBUG_ZOOMED_NEXT_ELSE", currentPhoto+"/"+temp);
            }
        }*/
        if(currentPhoto!=images.size()-1) {
            currentPhoto += 1;
            zoomedImage.setImageBitmap(images.get(currentPhoto).getBitmap());
        }
    }

    public void openSaveDialog() {
        SaveImageDialog dialog = new SaveImageDialog();
        dialog.show(getSupportFragmentManager(), "save image dialog");
    }

    public void openShareDialog() {
        ShareImageDialog dialog = new ShareImageDialog();
        dialog.show(getSupportFragmentManager(), "share image dialog");
    }

    private void saveImage(String fileName) {
        GalleryImage image = images.get(currentPhoto);
        if(!image.isSaved()) {
            Bitmap bitmap = image.getBitmap();
            File dir = new File(Environment.getExternalStorageDirectory() + "/GalleryImages");
            if(!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }
            String imageFileName ="";
            if(fileName.equals("")) {
                imageFileName = image.getTitle()+".jpg";
            } else { imageFileName = fileName+".jpg"; }
            File imageFile = new File(dir.getAbsolutePath() + "/"+imageFileName);
            ContentValues values = new ContentValues(4);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
            values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
            ContentResolver resolver = getContentResolver();
            Uri base = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri newUri = resolver.insert(base, values);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE), String.valueOf(newUri));
            image.saved();
            Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
        }

    }

    public void shareImage(String fileName) {
        //saveImage(fileName);
        Toast.makeText(this, "Image Uploaded to Drive", Toast.LENGTH_SHORT).show();
    }

    public void saveDialogSignal(String fileName) {
        saveImage(fileName);
    }

    public void shareDialogSignal(String fileName) {
        shareImage(fileName);
    }

    private void allSet(String toRemove) {
        Intent data = new Intent();
        data.putExtra("TO_REMOVE", toRemove);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        allSet(toRemove);
        super.onDestroy();
    }
}
