package com.techtrainingcamp_client_25.custom_layout;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.techtrainingcamp_client_25.Controller;
import com.techtrainingcamp_client_25.R;
import com.techtrainingcamp_client_25.model.Article;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private ArrayList<Article> mDataset = null;
    private IOnItemClickListener mItemClickListener;

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvPublishTime;
        private ImageView tvImageView;
        private LinearLayout multiImgLayout;
        private View contentView;

        public RecyclerViewHolder(View v) {
            super(v);
            contentView = v;
            tvTitle = v.findViewById(R.id.tv_title);
            tvAuthor = v.findViewById(R.id.tv_author);
            tvPublishTime = v.findViewById(R.id.tv_publish_time);
            tvImageView = v.findViewById(R.id.img_1);
            multiImgLayout = v.findViewById(R.id.multi_img_layout);
        }

        public void onBind(int position, Article data, View v) {
            tvTitle.setText(data.getTitle());
            tvAuthor.setText(data.getAuthor());
            tvPublishTime.setText(data.getPublishTime());

            if(tvImageView != null) {
                Picasso.get().load("file:///android_asset/" + data.getCoverName(0)).into(tvImageView);
            }
            else if(multiImgLayout != null) {

                int itemSize = (int)((Controller.width - 32 * Controller.density) / data.getCntCover() - 20);
                Log.i("TAG", "itemSize: " + itemSize);
                for(String s: data.getAllCoverName()) {
                    Log.i("TAG", "Adding " + s);
                    ImageView tmp = new ImageView(v.getContext());
                    tmp.setPadding(10,10,10,10);
                    Picasso.get().load("file:///android_asset/" + s)
                            .resize(itemSize,itemSize)
                            .centerCrop()
                            .into(tmp);
                    multiImgLayout.addView(tmp);
                }
            }
        }

        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            if (listener != null) {
                contentView.setOnLongClickListener(listener);
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    public RecyclerAdapter(ArrayList<Article> myDataset) {
        mDataset = myDataset;
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addData(int position, Article data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        if (position != mDataset.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mDataset.size() - position);
        }
    }

    public void removeData(int position) {
        if (null != mDataset && mDataset.size() > position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
            if (position != mDataset.size()) {
                //刷新改变位置item下方的所有Item的位置,避免索引错乱
                notifyItemRangeChanged(position, mDataset.size() - position);
            }
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("TAG","add");
        if(viewType == 1) {
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_type1, parent, false));
        }
        else if(viewType == 2) {
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_type2, parent, false));
        }
        else if(viewType == 3) {
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_type3, parent, false));
        }
        else if(viewType == 4){
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_type4, parent, false));
        }
        else {
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_type0, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.onBind(position, mDataset.get(position), holder.contentView);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, mDataset.get(position));
                }
            }
        });
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemLongCLick(position, mDataset.get(position));
                }
                return false;
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface IOnItemClickListener {

        void onItemCLick(int position, Article data);

        void onItemLongCLick(int position, Article data);
    }
}