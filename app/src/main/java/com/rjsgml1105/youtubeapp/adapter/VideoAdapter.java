package com.rjsgml1105.youtubeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rjsgml1105.youtubeapp.MainActivity;
import com.rjsgml1105.youtubeapp.PhotoActivity;
import com.rjsgml1105.youtubeapp.R;
import com.rjsgml1105.youtubeapp.model.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    Context context;
    ArrayList<Video> videoList;

    public VideoAdapter(Context context, ArrayList<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_row, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        Video video = videoList.get(position);

        holder.txtTitle.setText(video.title);
        holder.txtDescription.setText(video.description);
        Glide.with(context).load(video.mediumUrl).placeholder(R.drawable.baseline_ondemand_video_24).into(holder.imgThumb);

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle;
        TextView txtDescription;
        ImageView imgThumb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            txtTitle = itemView.findViewById(R.id.textTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgThumb = itemView.findViewById(R.id.imgThumb);


            imgThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //새로운 액티비티를 띄운다
                    // 몇번째 데이터인지 가저오기
                    int index = getAdapterPosition();
                    Video video = videoList.get(index);

                    Intent intent = new Intent(context, PhotoActivity.class);
                    intent.putExtra("highUrl",video.highUrl);
                    context.startActivity(intent);
                }
            });


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    Video video = videoList.get(index);

                    // https://www.youtube.com/watch?v=tnUslFhxRTs
                    String url = "https://www.youtube.com/watch?v" + video.videoId;

                    //메인액티비처럼 사용가능
                    ((MainActivity)context).openWebPage(url);

                }
            });

        }


    }


}
