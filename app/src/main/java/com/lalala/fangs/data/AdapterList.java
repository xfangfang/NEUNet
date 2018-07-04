package com.lalala.fangs.data;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lalala.fangs.neunet.R;

import java.util.ArrayList;

/**
 * Created by FANGs on 2017/7/29.
 */

public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder> {


    private ArrayList<User> mList;
    private OnItemClickListener listener;
    private Context context;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        View rootView;
        TextView textView_userName;
        private OnItemClickListener listener;


        public ViewHolder(View itemView,OnItemClickListener l) {
            super(itemView);
            listener = l;
            rootView = itemView;
            textView_userName = (TextView) itemView.findViewById(R.id.list_item_username);
            Typeface typeface = Typeface.createFromAsset(rootView.getContext().getAssets(),"BigJohnFang.ttf");
            textView_userName.setTypeface(typeface);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(listener != null){
                listener.onItemLongClick(v,getAdapterPosition());
                return true;
            }
            return false;
        }
    }


    public AdapterList(ArrayList<User> List) {
        mList = List;
    }

    public void update(ArrayList<User> List){
        notifyItemRangeRemoved(0,mList.size());
        mList = List;
        notifyDataSetChanged();
    }

    private static final String TAG = "AdapterBookList";
    ViewHolder holder;
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, listener);
        this.holder = holder;
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User user = mList.get(position);
        holder.textView_userName.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

