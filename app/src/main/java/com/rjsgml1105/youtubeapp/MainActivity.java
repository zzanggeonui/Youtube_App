package com.rjsgml1105.youtubeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rjsgml1105.youtubeapp.adapter.VideoAdapter;
import com.rjsgml1105.youtubeapp.config.config;
import com.rjsgml1105.youtubeapp.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    EditText editSearch;
    ImageView imgSearch;
    RecyclerView recyclerView;
    VideoAdapter adapter;
    ProgressBar progressBar;

    ArrayList<Video> videoList = new ArrayList<>();

    String keyword;
    String pageToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSearch = findViewById(R.id.editSearch);
        imgSearch = findViewById(R.id.imgSearch);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 맨 마지막 데이터가 화면에 보이면!!!!
                // 네트워크 통해서 데이터를 추가로 받아와라!!
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                // 스크롤을 데이터 맨 끝까지 한 상태.
                if(lastPosition + 1 == totalCount){
                    // 네트워크 통해서 데이터를 받아오고, 화면에 표시!
                    addNetworkData();
                }



            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = editSearch.getText().toString().trim();

                if(keyword.isEmpty()){
                    return;
                }

                // 네트워크 데이터 처리하는 함수 호출
                getNetworkData();

            }
        });

    }

    private void getNetworkData() {
        // 유투브 API 호출


        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = config.BASE_URL + config.PATH
                + "?key=" + config.API_KEY
                + "&part=snippet&q="+keyword
                +"&maxResults=20&type=video";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        videoList.clear();
                        pageToken = null;

                        try {

                            pageToken = response.getString("nextPageToken");

                            JSONArray items = response.getJSONArray("items");

                            for(int i = 0; i < items.length(); i++){
                                JSONObject item = items.getJSONObject(i);

                                String videoId = item.getJSONObject("id").getString("videoId");
                                String title = item.getJSONObject("snippet").getString("title");
                                String description = item.getJSONObject("snippet").getString("description");
                                String mediumUrl = item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                String highUrl = item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                Video video = new Video(videoId, title, description, mediumUrl, highUrl);

                                videoList.add(video);

                            }

                        } catch (JSONException e) {
                            Log.i("YOUTBUE",e.toString());
                            return;
                        }

                        if(adapter == null){
                            adapter = new VideoAdapter(MainActivity.this,videoList);
                            recyclerView.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);
    }

    private void addNetworkData(){

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = config.BASE_URL + config.PATH
                + "?key=" + config.API_KEY
                + "&part=snippet&q="+keyword
                +"&type=video&maxResults=20&pageToken="+pageToken;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);

                        try {

                            pageToken = response.getString("nextPageToken");

                            JSONArray items = response.getJSONArray("items");

                            for(int i = 0; i < items.length(); i++){
                                JSONObject item = items.getJSONObject(i);

                                String videoId = item.getJSONObject("id").getString("videoId");
                                String title = item.getJSONObject("snippet").getString("title");
                                String description = item.getJSONObject("snippet").getString("description");
                                String mediumUrl = item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                String highUrl = item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                Video video = new Video(videoId, title, description, mediumUrl, highUrl);

                                videoList.add(video);

                            }

                        } catch (JSONException e) {
                            return;
                        }


                            adapter.notifyDataSetChanged();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        progressBar.setVisibility(View.VISIBLE);
        queue.add(request);


    }


    // 어댑터는 액티비티가 아니니가 메인에다가 한다.
    public void openWebPage(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }
}