package com.huyn.demogroup.lottie;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/8/24.
 */

public class SimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public SimpleAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SimpleHolder) holder).init(position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    private static class SimpleHolder extends RecyclerView.ViewHolder {

        private View mView;
        public SimpleHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.item_view);
        }
        public void init(int position) {
            int color = Color.WHITE;
            switch (position%4) {
                case 0:
                    color = Color.YELLOW;
                    break;
                case 1:
                    color = Color.BLUE;
                    break;
                case 2:
                    color = Color.GREEN;
                    break;
                default:
                    break;
            }
            mView.setBackgroundColor(color);
        }
    }

}
