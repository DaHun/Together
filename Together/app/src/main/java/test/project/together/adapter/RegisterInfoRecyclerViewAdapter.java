package test.project.together.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import test.project.together.R;
import test.project.together.model.ChangeEvent;
import test.project.together.model.Matching;
import test.project.together.tab.RegisterInfoFragment;
import test.project.together.viewholder.RegisterInfoViewHolder;

/**
 * Created by jeongdahun on 2017. 6. 26..
 */

public class RegisterInfoRecyclerViewAdapter extends RecyclerView.Adapter<RegisterInfoViewHolder>{
///TEST-HOJIN
    ArrayList<Matching> items;
    Context context;

    public RegisterInfoRecyclerViewAdapter(ArrayList<Matching> items){
        this.items=items;
    }

    @Override
    public RegisterInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_register,parent,false);
        context=parent.getContext();

        return new RegisterInfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RegisterInfoViewHolder holder, final int position) {
        final Matching item=items.get(position);

        if(item.isCheck()==1)
            holder.oxImage.setImageResource(R.drawable.o);
        else
            holder.oxImage.setImageResource(R.drawable.x);


        holder.date.setText(item.getDate());
        holder.startTime.setText(item.getStartTime());
        holder.finishTime.setText(item.getFinishTime());

        holder.wish.setText(item.getWish());

        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterInfoFragment.matching_id=item.getMatching_id();
                ViewPagerAdapter.subMode=3;
                EventBus.getDefault().post(new ChangeEvent());
            }
        });
    }


    @Override
    public int getItemCount() {
        if(items != null)
            return items.size();
        else
            return 0;
    }
}
