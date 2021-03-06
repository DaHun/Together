package test.project.together.tab;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.project.together.R;
import test.project.together.adapter.ViewPagerAdapter;
import test.project.together.application.ApplicationController;
import test.project.together.main.MainActivity;
import test.project.together.model.ChangeEvent;
import test.project.together.network.NetworkService;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jeongdahun on 2017. 9. 11..
 */

public class SNSplusFragment extends Fragment{

    //@BindView(R.id.postingRecyclerView) RecyclerView postingRecyclerView;
    //@BindView(R.id.previousBtn) Button previousBtn;
    //@BindView(R.id.plusposting) Button plusregi;
    @BindView(R.id.registerposting) Button registerposting;
    @BindView(R.id.getphotobtn)
    ImageButton getphotobtn;
    @BindView(R.id.newposttxt)
    EditText newposttxt;
    @BindView(R.id.newImage) ImageView newImage;
    @BindView(R.id.newtextrecord) Button textrecordbtn;

    final String TAG="SNSplusFragment";
    LinearLayout layout;
    NetworkService service;

    Uri data = null;
    String fileName;
    final int REQ_CODE_SELECT_IMAGE=100;

    private final int REQ_CODE_SPEECH_INPUT = 200;
    String txt="";

    //ArrayList<Posting> postingList;
    //PostingRecyclerViewAdapter postingRecyclerViewAdapter;
    LinearLayoutManager linearLayoutManager;

    public static String imgPath=null;
    public SNSplusFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_plusposting, container, false);

        ButterKnife.bind(this, layout);

        initSetting();

        return layout;
    }

    public void initSetting() {

        service= ApplicationController.getInstance().getNetworkService();

        //사진 가져오기
        getphotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        //텍스트 음성 입력
        textrecordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askSpeechInput();
            }
        });


        //게시물 추가
        registerposting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ApplicationController.user_id));
                RequestBody content = RequestBody.create(MediaType.parse("multipart/form-data"),newposttxt.getText().toString());
                RequestBody date = RequestBody.create(MediaType.parse("multipart/form-data"), getDateString());


                MultipartBody.Part body;

                if (data == null) {
                    body = null;
                } else {

                    /**
                     * 비트맵 관련한 자료는 아래의 링크에서 참고
                     * http://mainia.tistory.com/468
                     */

                    /*
                    이미지를 리사이징하는 부분입니다.
                    리사이징하는 이유!! 안드로이드는 메모리에 민감하다고 세미나에서 말씀드렸습니다~
                    구글에서는 최소 16MByte로 정하고 있으나, 제조사 별로 또 디바이스별로 메모리의 크기는 다릅니다.
                    또한, 이미지를 서버에 업로드할 때 이미지의 크기가 크다면 시간이 오래 걸리고 데이터 소모가 큽니다!!
                     */
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inSampleSize = 4; //얼마나 줄일지 설정하는 옵션 4--> 1/4로 줄이겠다

                    InputStream in = null; // here, you need to get your context.
                    try {
                        in = MainActivity.contentResolver.openInputStream(data);
                        Log.d(TAG,"inputstream");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //inputstream 형태로 받은 이미지로 부터 비트맵을 만들어 바이트 단위로 압축 그 이후 스트림 배열에 담아서 전송합니다.

                    Bitmap bitmap = BitmapFactory.decodeStream(in, null, options); // InputStream 으로부터 Bitmap 을 만들어 준다.
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    // 압축 옵션( JPEG, PNG ) , 품질 설정 ( 0 - 100까지의 int형 ), 압축된 바이트 배열을 담을 스트림
                    RequestBody photoBody = RequestBody.create(MediaType.parse("image/jpeg"), baos.toByteArray());

                    //File photo = new File(image_path); // 가져온 파일의 이름을 알아내려고 사용합니다

                    // MultipartBody.Part 실제 파일의 이름을 보내기 위해 사용!!
                    body = MultipartBody.Part.createFormData("image", fileName, photoBody);


                }

                // 파일과 텍스트를 함께 넘길 때는 multipart를 사용합니다.


                Call<Void> snsPlus = service.snsPlus(body, user_id, content, date);
                snsPlus.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Log.d(TAG,"success");
                            ViewPagerAdapter.SNSmode=0;
                            EventBus.getDefault().post(new ChangeEvent());
                        }
                        else
                            Log.d(TAG,"fail1");

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(TAG,"fail2");

                    }
                });



            }
        });



        //이전 버튼 클릭 리스너
        /*
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Matching());
            }
        });

*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     //   Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== RESULT_OK)
            {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());

                    this.data = data.getData();

                    if (this.data.getScheme().equals("file")) {
                        fileName = this.data.getLastPathSegment();
                    } else {
                        Cursor cursor = null;
                        try {
                            cursor = MainActivity.contentResolver.query(this.data, new String[]{
                                    MediaStore.Images.ImageColumns.DISPLAY_NAME
                            }, null, null, null);

                            if (cursor != null && cursor.moveToFirst()) {
                                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                                Log.d(TAG, "name is " + fileName);
                            }
                        } finally {

                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }

                    Toast.makeText(getContext(),fileName,Toast.LENGTH_SHORT).show();
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                    //배치해놓은 ImageView에 set
                    newImage.setImageBitmap(image_bitmap);

                    Log.d(TAG,this.data+"");


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txt=newposttxt.getText().toString();
                newposttxt.setText(txt+result.get(0)+" ");
            }
        }
    }

    public String getDateString()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String str_date = df.format(new Date());

        return str_date;
    }

    //음성
    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }


}
