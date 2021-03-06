package test.project.together.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.project.together.R;
import test.project.together.adapter.RegisterInfoRecyclerViewAdapter;
import test.project.together.application.ApplicationController;
import test.project.together.model.Matching;
import test.project.together.network.NetworkService;

/**
 * Created by jeongdahun on 2017. 9. 11..
 */

public class CheckFragment extends Fragment
{
    @BindView(R.id.registerInfoRecyclerview) RecyclerView registerInfoRecyclerView;

    NetworkService service; 
    final String TAG="CheckFragment";
    LinearLayout layout;

    ArrayList<Matching> matchingList;
    RegisterInfoRecyclerViewAdapter registerInfoRecyclerViewAdapter;
    LinearLayoutManager linearLayoutManager;

    public CheckFragment() {
        super();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_check, container, false);

        ButterKnife.bind(this, layout);

        initSetting();

        return layout;
    }

    public void initSetting() {


        service= ApplicationController.getInstance().getNetworkService();

        Call<ArrayList<Matching>> load_AllRegisterInfo=service.load_allRegisterInfo(ApplicationController.user_id);
        load_AllRegisterInfo.enqueue(new Callback<ArrayList<Matching>>() {
            @Override
            public void onResponse(Call<ArrayList<Matching>> call, Response<ArrayList<Matching>> response) {
                if(response.isSuccessful()){
                    matchingList=response.body();
                    Log.d(TAG,matchingList.size()+" ");

                    //RecyclerView Setting
                    registerInfoRecyclerViewAdapter=new RegisterInfoRecyclerViewAdapter(matchingList);
                    registerInfoRecyclerView.setAdapter(registerInfoRecyclerViewAdapter);
                    linearLayoutManager=new LinearLayoutManager(getContext());
                    registerInfoRecyclerView.setLayoutManager(linearLayoutManager);

                }else
                    Log.d(TAG,"fail1");

            }

            @Override
            public void onFailure(Call<ArrayList<Matching>> call, Throwable t) {
                Log.d(TAG,"fail2");

            }
        });



    }



}
