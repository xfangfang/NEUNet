package com.lalala.fangs.neunet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lalala.fangs.data.User;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private String userStr,paswdStr;
    private EditText activityAddUserEditTextUsername;
    private EditText activityAddUserEditTextPassword;
    private Button activityAddUserBtnAdd;
    private TextView loginText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("添加账号");
        setContentView(R.layout.activity_login);

        activityAddUserEditTextUsername = (EditText) findViewById(R.id.activityAddUser_editText_username);
        activityAddUserEditTextPassword = (EditText) findViewById(R.id.activityAddUser_editText_password);
        activityAddUserBtnAdd = (Button) findViewById(R.id.activityAddUser_btn_add);
        loginText = (TextView) findViewById(R.id.login_text);

        loginText.setShadowLayer(10, 0, 0, Color.parseColor("#80555555"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#9e9e9e"));
        }

    }


    public void login(View view){
        if(normalChack()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", userStr);
            intent.putExtra("password", paswdStr);
            User user = new User(userStr,paswdStr);

            //统计添加用户次数
            HashMap<String, String> map = new HashMap<>();
            map.put("user", userStr);
            map.put("year",String.valueOf(user.getYear()));
            MobclickAgent.onEvent(LoginActivity.this, "addUser", map);

            setResult(101, intent);
            finish();
        }
    }

    private boolean normalChack(){
        userStr = activityAddUserEditTextUsername.getText().toString();
        paswdStr = activityAddUserEditTextPassword.getText().toString();
        if(userStr.equals("") || paswdStr.equals("")){
            Toast.makeText(getApplicationContext(),"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
}
