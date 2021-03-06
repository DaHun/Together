package test.project.together.tab;

import android.Manifest;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.project.together.R;
import test.project.together.adapter.ViewPagerAdapter;
import test.project.together.model.ChangeEvent;

/**
 * Created by jeongdahun on 2017. 9. 11..
 */

public class SeniorFragment extends Fragment {

   // @BindView(R.id.nextBtn) Button nextBtn;
    @BindView(R.id.registerBtn) Button registerBtn;
    @BindView(R.id.checkBtn) Button checkBtn;

    ///////

    final static String TAG="SeniorFragment";
    LinearLayout layout;



    public SeniorFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_senior, container, false);

        ButterKnife.bind(this, layout);

        initSetting();

        return layout;
    }



    public void initSetting() {



        //Button
        /*
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Posting());
            }
        });
*/
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int permissionResult = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                    /**
                     * 패키지는 안드로이드 어플리케이션의 아이디이다.
                     * 현재 어플리케이션이 ACCESS_FINE_LOCATION에 대해 거부되어있는지 확인한다.
                     * */
                    if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        /** * 사용자가 ACCESS_FINE_LOCATION 권한을 거부한 적이 있는지 확인한다.
                         * 거부한적이 있으면 True를 리턴하고
                         * 거부한적이 없으면 False를 리턴한다. */
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Toast.makeText(getActivity().getApplication(), "LOCATION permissions are required. Please change your permission settings.", Toast.LENGTH_LONG).show();
                        } // 최초로 권한을 요청할 때
                        else { // ACCESS_FINE_LOCATION 권한을 Android OS에 요청한다.
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                        }
                    } // ACCESS_FINE_LOCATION 권한이 있을 때
                    else { // 즉시 실행
                        //RegisterFragment로 이동
                        ViewPagerAdapter.subMode=1;
                        EventBus.getDefault().post(new ChangeEvent());

                    }
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPagerAdapter.subMode=2;
                EventBus.getDefault().post(new ChangeEvent());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            try {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "LOCATION permission athorized", Toast.LENGTH_SHORT).show();
                            ViewPagerAdapter.subMode = 1;
                            EventBus.getDefault().post(new ChangeEvent());
                        } else {
                            Toast.makeText(getContext(), "LOCATION permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }catch (Exception e){
                Log.d(TAG, "fail~~~~~~~~~~~~~~~");
            }
        }
    }
}
