package com.lalala.fangs.neunet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lalala.fangs.data.User;
import com.lalala.fangs.utils.NeuNet;
import com.lalala.fangs.utils.State;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import static com.lalala.fangs.utils.State.getStateMap;

public class FloatForceActivity extends AppCompatActivity {

    private ImageButton imgBtnCancel;
    private RelativeLayout rl;
    private ProgressBar floatActivityPb;
    private TextView floatActivityText;

    private User currentUser;
    private SparseArray res;
    private NeuNet neuClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_force);
        imgBtnCancel = (ImageButton) findViewById(R.id.floatForceImgBtn_cancel);
        rl = (RelativeLayout) findViewById(R.id.floatForceActivity_root);
        floatActivityPb = (ProgressBar) findViewById(R.id.floatForceActivity_pb);
        floatActivityText = (TextView) findViewById(R.id.floatForceActivity_text);

        res = getStateMap();


        neuClient = new NeuNet(getApplicationContext());

        neuClient.setOnLoginExitStateListener(new NeuNet.OnLoginExitStateListener() {
            @Override
            public void getState(int state) {
                if(state == State.LOGIN_SUCCESS){
                    rl.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Toast.makeText(getApplicationContext(),"已经登录校园网",Toast.LENGTH_SHORT).show();
                        }
                    },500);
                }else if(state == State.ALREADY_CONNECTED){
                    neuClient.exit(currentUser.getUsername(),currentUser.getPassword(),true);
                }else if(state == State.ALL_EXIT_SUCCESS ||
                        state == State.EXIT_SUCCESS ||
                        state == State.NOT_CONNECTED){
                    setText("已经退出所有设备\n正在登录中...");
                    rl.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            neuClient.login(currentUser.getUsername(),currentUser.getPassword(),"Android");
                        }
                    }, 1000);
                }else{
                    setText((String)res.get(state));
                    floatActivityPb.setVisibility(View.INVISIBLE);
                }
            }
        });
        neuClient.setOnInforListener(new NeuNet.OnInforListener() {
            @Override
            public void getInfor(String flow, String money, String ip) {
                if(flow == null){
                    currentUser = getRecentUser();
                    if(currentUser == null) {
                        setText("请先去主程序添加账号");
                        floatActivityPb.setVisibility(View.INVISIBLE);
                    }else{
                        setText("登录中...");
                        neuClient.login(currentUser.getUsername(),currentUser.getPassword(),"Android");
                    }
                }else{
                    setText("登录中...");
                    rl.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Toast.makeText(getApplicationContext(),"已经登录校园网",Toast.LENGTH_SHORT).show();
                        }
                    },500);
                }
            }
        });
        currentUser = getRecentUser();
        if(currentUser != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("user", currentUser.getUsername());
            map.put("year", String.valueOf(currentUser.getYear()));
            MobclickAgent.onEvent(FloatForceActivity.this, "floatForceLogin", map);
        }
        neuClient.getInfor();


        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private static final String TAG = "FloatForceActivity";
    @Override
    public void onBackPressed() {
        //do nothing
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private User getRecentUser() {
        return User.getRecentLoginUser(FloatForceActivity.this);
    }

    private void setText(String t){
        floatActivityText.setText(t);
    }
}
