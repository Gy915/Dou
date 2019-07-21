package com.bytedance.androidcamp.network.dou;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.androidcamp.network.dou.api.IMiniDouyinService;
import com.bytedance.androidcamp.network.dou.model.Video;
import com.bytedance.androidcamp.network.dou.util.CustomCameraActivity;
import com.bytedance.androidcamp.network.dou.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int GRANT_PERMISSION = 3;
    private static final int REQUEST_CUSTOM_CAMERA = 101;
    private static final int REQUEST_READ_STORAGE = 102;
    private static final String TAG = "MainActivity";
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    public Button mBtn_video;
    private Button mBtnRefresh;
    public MultipartBody.Part cover_image;
    public MultipartBody.Part video;
    public boolean judege=false;


    // TODO 8: initialize retrofit & miniDouyinService

    private Retrofit retrofit;
    private IMiniDouyinService miniDouyinService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
        initBtns();
        fetchFeed(mBtnRefresh);
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                Intent intent=new Intent(MainActivity.this,Exercises3.class);
                startActivity(intent);
                break;
            case R.id.remove_item:

                    Toast.makeText(this,"offline!/online!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_up:
                    Intent intent2=new Intent(MainActivity.this,sign_up.class);
                    startActivity(intent2);
                    break;

        }
        return true;
    }

    private boolean requestReadExternalStoragePermission(String explanation) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "You should grant external storage permission to continue " + explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 3);
            }
            return false;
        } else {
            return true;
        }
    }
    private void initBtns() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    if (requestReadExternalStoragePermission("select an image")) {
                        chooseImage();
                    }
                } else if (getString(R.string.select_a_video).equals(s)) {
                    if (requestReadExternalStoragePermission("select a video")) {
                        chooseVideo();
                    }
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = "
                                + mSelectedVideo
                                + ", mSelectedImage = "
                                + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });
        mBtnRefresh = findViewById(R.id.btn_refresh);
        mBtn_video = findViewById(R.id.btn_video);
        mBtn_video.setOnClickListener(v -> {
            //检查权限
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},REQUEST_CUSTOM_CAMERA);
            }else{
                startActivity(new Intent(MainActivity.this, CustomCameraActivity1.class));
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView UserId;
        public TextView UserName;
        public ImageView like;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            like=itemView.findViewById(R.id.like);
            img = itemView.findViewById(R.id.img);
            UserId = itemView.findViewById(R.id.UserId);
            UserName =itemView.findViewById(R.id.UserName);
        }

        public void bind(final Activity activity, final Video video) {
            ImageHelper.displayWebImage(video.getImageUrl(), img);
            UserId.setText("----"+video.getStudentId());
            UserName.setText("@"+video.getUserName());
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoActivity.launch(activity, video.getVideoUrl());
                }
            });

            final boolean[] judge = {false};
            like.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    judge[0] =!judge[0];
                    if(judge[0])
                    {
                        like.setColorFilter(Color.RED);
                    }
                    else {
                        like.setColorFilter(Color.WHITE);
                    }
                }
            });

        }
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MyViewHolder(
                        LayoutInflater.from(MainActivity.this)
                                .inflate(R.layout.video_item_view, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                viewHolder.bind(MainActivity.this, video);
            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(MainActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public class nothing{

    }
    public class results{
        nothing result;
        String url;
        boolean success;

    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);

        // TODO 9: post video & update buttons
        Log.d("postVideo","prepare_1");
        Call<results> call = getDouyinService().PostVideo("1120170736","Gy",coverImagePart,videoPart);
        Log.d("postVideo","prepare_2");
        call.enqueue(new Callback<results>() {
            @Override
            public void onResponse(Call<results> call, Response<results> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mBtn.setText(R.string.success_try_refresh);
                    mBtn.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<results> call, Throwable t) {
                Toast.makeText(MainActivity.this, "retrofit: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public  class Feed{
        String student_id;
        String user_name;
        String image_url;
        String _id;
        String video_url;
        String createAt;
        String updatedAt;
    }

    public class Feeds{
        List<Video> feeds;
        boolean success;
    }
    public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);
        // TODO 10: get videos & update recycler list
        Call<Feeds> call = getDouyinService().getVideo();
        call.enqueue(new Callback<Feeds>() {
            @Override
            public void onResponse(Call<Feeds> call, Response<Feeds> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mVideos=response.body().feeds;
                    mRv.getAdapter().notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "已刷新", Toast.LENGTH_SHORT).show();
                    mBtnRefresh.setText("reflesh feed");
                    mBtnRefresh.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Feeds> call, Throwable t) {
                Toast.makeText(MainActivity.this, "retrofit: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private IMiniDouyinService getDouyinService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(IMiniDouyinService.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (miniDouyinService == null) {
            miniDouyinService = retrofit.create(IMiniDouyinService.class);
        }
        return miniDouyinService;
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CUSTOM_CAMERA: {
                // 判断权限是否已经授予
                for(int i = 0; i < grantResults.length;i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
                break;
            }
            case REQUEST_READ_STORAGE:{
                for(int i = 0; i < grantResults.length;i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }

            }
        }
    }
}
