package com.shasun.staffportal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.v4.content.CursorLoader;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadFiles extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;
    private TextView textView;
    private TextView textViewResponse;
    private Handler handler = new Handler();
    private static final int SELECT_VIDEO = 3;

    private String selectedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_upload);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        textView = (TextView) findViewById(R.id.textView);
        textViewResponse = (TextView) findViewById(R.id.textViewResponse);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

//    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedImageUri = data.getData();
                selectedImageUri = handleImageUri(selectedImageUri);
                selectedPath = getPath(selectedImageUri);
                textView.setText(selectedPath);
            }
        }
    }

    public static Uri handleImageUri(Uri uri) {
        if (uri.getPath().contains("content")) {
            Pattern pattern = Pattern.compile("(content://media/.*\\d)");
            Matcher matcher = pattern.matcher(uri.getPath());
            if (matcher.find())
                return Uri.parse(matcher.group(1));
            else
                throw new IllegalArgumentException("Cannot handle this URI");
        }
        return uri;
    }

    @SuppressLint("NewApi")
    public String getPath(Uri uri) {
        Cursor cursor = null;
        String path = "";
//        String wholeID = DocumentsContract.getDocumentId(uri);
//
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//
//        String[] column = {MediaStore.Images.Media.DATA};
//
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                column, sel, new String[]{id}, null);
//
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }

        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
//            cursor.moveToFirst();
            String document_id = "";
            if (cursor != null && cursor.moveToFirst()) {
                document_id = cursor.getString(0);
            }
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            //MediaStore.Video.Media._ID + " = ? "
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri,
                    proj, MediaStore.Video.Media._ID + " = ", new String[]{document_id}, null);

            if (cursor != null && cursor.moveToFirst()) {
//            cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
            cursor.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if (cursor != null){
                cursor.close();
            }
        }
        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadFiles.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }


    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            chooseVideo();
        }
        if (v == buttonUpload) {
            uploadVideo();
        }
    }
}
