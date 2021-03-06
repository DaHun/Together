package test.project.together.tab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.project.together.R;
import test.project.together.application.ApplicationController;
import test.project.together.model.Matching;
import test.project.together.model.User;
import test.project.together.network.NetworkService;

/**
 * Created by jeongdahun on 2017. 9. 11..
 */

public class VolMatchingInfoFragment extends Fragment {

   // @BindView(R.id.nextBtn) Button nextBtn;

    @BindView(R.id.vollocationText) TextView locationText;
    @BindView(R.id.volwantText) TextView wantText;
    @BindView(R.id.voldateText) TextView dateText;

    @BindView(R.id.volstartTimeText) TextView startTimeText;
    @BindView(R.id.volfinishTimeText) TextView finishTimeText;

    @BindView(R.id.seniorName) TextView seniorNameText;
    @BindView(R.id.seniorAge) TextView seniorAgeText;
    @BindView(R.id.seniorPhone) TextView seniorPhoneText;

    @BindView(R.id.callButton)
    Button callButton;

    LinearLayout layout;
    NetworkService service;
    final static String TAG="VolMatchingInfoFragment";

    public static int matching_id;
    public VolMatchingInfoFragment() {
        super();
    }

    String phoneNumber = "tel:";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_volregisterinfo, container, false);

        ButterKnife.bind(this, layout);

        initSetting();

        return layout;
    }

    public void initSetting() {

        service = ApplicationController.getInstance().getNetworkService();

        Call<Matching> load_oneRegisterInfo = service.load_oneRegisterInfo(matching_id);
        load_oneRegisterInfo.enqueue(new Callback<Matching>() {
            @Override
            public void onResponse(Call<Matching> call, Response<Matching> response) {
                if (response.isSuccessful()) {
                    locationText.setText(response.body().getLocation());
                    wantText.setText(response.body().getWish());
                    dateText.setText(response.body().getDate());
                    startTimeText.setText(response.body().getStartTime());
                    finishTimeText.setText(response.body().getFinishTime());

                    Log.d(TAG, response.body().isCheck() + "");
                    //matching 됬을경우에만 쿼리날림
                    if(response.body().isCheck()==1){
                        matchingInfo();
                    }else{
                        callButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Matching> call, Throwable t) {

            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                //               startActivity(in);
                /**
                 * 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 확인한다.
                 * Android는 C언어 기반으로 만들어졌기 때문에 Boolean 타입보다 Int 타입을 사용한다.
                 * */
                int permissionResult = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                /**
                 * 패키지는 안드로이드 어플리케이션의 아이디이다.
                 * 현재 어플리케이션이 CALL_PHONE에 대해 거부되어있는지 확인한다.
                 * */
                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    /** * 사용자가 CALL_PHONE 권한을 거부한 적이 있는지 확인한다.
                     * 거부한적이 있으면 True를 리턴하고
                     * 거부한적이 없으면 False를 리턴한다. */
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(getActivity().getApplication(), "CALL permissions are required. Please change your permission settings.", Toast.LENGTH_LONG).show();
                    } // 최초로 권한을 요청할 때
                    else { // CALL_PHONE 권한을 Android OS에 요청한다.
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    }
                } // CALL_PHONE의 권한이 있을 때
                else { // 즉시 실행
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                    startActivity(intent); }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) { // 요청한 권한을 사용자가 "허용" 했다면...
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.CALL_PHONE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "CALL permission is authorized", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "CALL permission is denied", Toast.LENGTH_SHORT).show();
                        callButton.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    public void matchingInfo(){

        Call<User> load_matchingInfo2=service.load_matchinginfo2(matching_id);
        load_matchingInfo2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,"success");
                    seniorNameText.setText(response.body().name);
                    seniorAgeText.setText(response.body().age);
                    seniorPhoneText.setText(response.body().phone);
                    phoneNumber += response.body().phone;
                }
                else
                    Log.d(TAG,"fail1");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG,"fail2");
            }
        });
    }
}
