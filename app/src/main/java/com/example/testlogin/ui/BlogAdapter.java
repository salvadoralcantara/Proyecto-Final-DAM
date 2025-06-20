package com.example.testlogin.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testlogin.R;
import com.example.testlogin.model.Blog;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.VH> {

    private List<Blog> data;

    public void setData(List<Blog> list) {
        data = list;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p,int v){
        View view= LayoutInflater.from(p.getContext())
                .inflate(R.layout.blog_item,p,false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h,int pos){
        Blog b=data.get(pos);
        h.title.setText(b.title);
        h.story.setText(b.story);
    }
    @Override public int getItemCount(){ return data==null?0:data.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView title,story;
        VH(@NonNull View v){
            super(v);
            title=v.findViewById(R.id.tvItemTitle);
            story=v.findViewById(R.id.tvItemStory);
        }
    }
}
