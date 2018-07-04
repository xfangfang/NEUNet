package com.lalala.fangs.neunet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lalala.fangs.data.AdapterList;
import com.lalala.fangs.data.User;
import com.lalala.fangs.utils.GetJianshuMessage;
import com.lalala.fangs.utils.NeuNet;
import com.lalala.fangs.utils.NeuNetworkCenter;
import com.lalala.fangs.utils.State;
import com.lalala.fangs.view.ShadowView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

import static com.lalala.fangs.utils.State.ALREADY_CONNECTED;
import static com.lalala.fangs.utils.State.LOGIN_SUCCESS;
import static com.lalala.fangs.utils.State.PC_LOGIN_SUCCESS;
import static com.lalala.fangs.utils.State.WRONG_PASSWORD;
import static com.lalala.fangs.utils.State.WRONG_USERNAME;


public class MainActivity extends AppCompatActivity {
    ProgressBar pb;
    NeuNet neuNetClient;
    NeuNetworkCenter neuNetworkCenter;
    SparseArray<String> res;
    String MessageUrl;
    String DownloadUrl;
    private ImageButton statusImgBtn;
    private TextView card_text_ip;
    private TextView cardLogin_text_status;
    private TextView textFlow;
    private TextView textMoney;
    private FloatingActionButton fabUsers;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton fabMore;
    private FloatingActionButton fabSetting;

    private LinearLayout layoutRoot;
    private RecyclerView recyclerView_listUsers;
    private TextView textUsername;
    private ShadowView cardLoginStatus;
    private ImageButton imageBtnAddUser;
    private Button dialogDelete;
    private EditText dialogEditTextPassword;
    private Button dialogButtonFind;
    private Button dialogButtonOk;
    private AlertDialog dlg;
    private TextView dialogUsername;
    private TextView textMessage;
    private ImageView ImageBackground;
    private TextView textHint;
    private String FIRSTTIME = "firstTime3.0";




    private enum STATUS {ONLINE, OFFLINE, NULL}

    private ArrayList<User> userList;
    private User currentUser;
    private User editUser;
    private AdapterList adapterList;
    private GetJianshuMessage getJianshuMessage;

    private PushAgent mPushAgent;
    ScaleAnimation animationUp, animationDown;
    private final String SETTING = "SETTING";
    private boolean isAutoLogin;
    private boolean isPCLogin;
    private boolean isFirstTime;

    STATUS Status = STATUS.NULL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            MainActivity.this.getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            MainActivity.this.getWindow().setStatusBarColor(Color.parseColor("#9e9e9e"));
        }

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
        initSP();

        initView();
        initData();

        initSP();
        startWaiting();
        neuNetClient.getInfor();

        PushAgent.getInstance(MainActivity.this).onAppStart();

    }

    private void initSP() {
        SharedPreferences sp = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        isPCLogin = sp.getBoolean("pcLogin2", true);
//        isAutoLogin = sp.getBoolean("autoLogin", false);
        isAutoLogin = false;
        isFirstTime = sp.getBoolean(FIRSTTIME, true);

        if (statusImgBtn != null) {
            statusImgBtn.setClickable(isPCLogin);
        }

        if(isFirstTime && !firstOpenToday){
            guide();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        neuNetworkCenter.clearCookie();
        if (requestCode == 100 && resultCode == 101) {
            String userStr = data.getStringExtra("username");
            String passwordStr = data.getStringExtra("password");
            if (userStr != null) {
                if (!userStr.equals("")) {
                    User user = new User(userStr, passwordStr);
                    user.save(MainActivity.this);
                    getUserList();
                    setRecentUser(user);
                    loginAndroid(currentUser);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        startWaiting();

        neuNetClient.getInfor();

        currentUser = getRecentUser();
        if (currentUser == null) {
            if (userList.size() != 0) {
                currentUser = userList.get(0);
            }
        }
        initSP();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.someNew:
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);  //(普通消息框)
                ab.setTitle("muu~muua~");
                ab.setMessage("\n欢乐的时光总是短暂的，希望这个应用能在东大流传下去\n");
                AlertDialog dialog = ab.create();
                dialog.show();
                break;
            case R.id.menu_contact:
                Uri uri = Uri.parse("https://github.com/xfangfang");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.finish:
                finish();
                break;
            default:
        }
        return true;
    }

    private void initView() {
        ImageBackground = (ImageView) findViewById(R.id.image);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        textFlow = (TextView) findViewById(R.id.text_flow);
        textMoney = (TextView) findViewById(R.id.text_money);
        cardLogin_text_status = (TextView) findViewById(R.id.card_login_text_status);
        card_text_ip = (TextView) findViewById(R.id.card_text_ip);
        fabUsers = (FloatingActionButton) findViewById(R.id.fab_users);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.floatingActionsMenu);
        fabMore = (FloatingActionButton) findViewById(R.id.fab_more);
        fabSetting = (FloatingActionButton) findViewById(R.id.fab_setting);
        layoutRoot = (LinearLayout) findViewById(R.id.layout_root);
        recyclerView_listUsers = (RecyclerView) findViewById(R.id.list_users);
        textUsername = (TextView) findViewById(R.id.text_username);
        cardLoginStatus = (ShadowView) findViewById(R.id.card_login_status);
        imageBtnAddUser = (ImageButton) findViewById(R.id.imageBtn_addUser);
        statusImgBtn = (ImageButton) findViewById(R.id.status_imgBtn);
        textMessage = (TextView) findViewById(R.id.text_message);
        textHint = (TextView) findViewById(R.id.text_hint);

        statusImgBtn.setClickable(isPCLogin);
        textUsername.setShadowLayer(10, 0, 0, Color.parseColor("#80555555"));
        Typeface typeface = Typeface.createFromAsset(MainActivity.this.getAssets(), "BigJohnFang.ttf");
        textUsername.setTypeface(typeface);
        textFlow.setTypeface(typeface);
        textMoney.setTypeface(typeface);
        cardLogin_text_status.setTypeface(typeface);

        fabUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstTime) return;
                showUserList();
                floatingActionsMenu.collapse();
            }
        });
        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstTime) return;
                if (currentUser == null) {
                    Toast.makeText(getApplicationContext(), "请先添加一个账号", Toast.LENGTH_SHORT).show();
                    addUser();
                } else {
                    if (neuNetClient.netCheck()) {
                        Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                        intent.putExtra("user", currentUser);
                        startActivity(intent);
                    }
                }
                floatingActionsMenu.collapse();

            }
        });
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstTime) return;

                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                floatingActionsMenu.collapse();

            }
        });


        imageBtnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        animationUp = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationUp.setDuration(100);
        animationUp.setFillAfter(true);

        animationDown = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationDown.setDuration(100);
        animationDown.setFillAfter(true);

        statusImgBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isPCLogin) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            v.startAnimation(animationUp);
                            break;
                        case MotionEvent.ACTION_DOWN:
                            v.startAnimation(animationDown);
                            break;
                    }
                }
                return false;
            }
        });

        statusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Status == STATUS.ONLINE) {
                    if (currentUser != null) {
                        exitAll(currentUser);
                    }
                } else if (Status == STATUS.OFFLINE) {
                    if (currentUser != null) {
                        loginPc(currentUser);
                    }
                }
            }
        });

        textUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        v.startAnimation(animationUp);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(animationDown);
                        break;
                }
                return false;
            }
        });

        textUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    exitAll(currentUser);
                }
            }
        });

        guide();
    }

    private ChainTourGuide mTourGuideHandler;

    private void guide() {

        if (!isFirstTime) return;

        floatingActionsMenu.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        floatingActionsMenu.expand();
                    }
                });
            }
        },800);

        Animation animation = new TranslateAnimation(0f, 0f, 0f, 0f);
        animation.setDuration(0);
        animation.setFillAfter(true);


        ChainTourGuide tourGuide0 = ChainTourGuide.init(MainActivity.this)
                .setToolTip(new ToolTip()
                        .setTitle("用户名")
                        .setDescription("点击下线全部设备")
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setShadow(false)
                        .setGravity(Gravity.RIGHT)
                        .setEnterAnimation(animation)
                )
                .playLater(textUsername);

        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("点击卡片切换登录状态")
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setGravity(Gravity.BOTTOM)
                        .setShadow(false)
                        .setEnterAnimation(animation)

                )
                .playLater(cardLogin_text_status);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("用户余额")
                        .setDescription("点击这个卡片同样切换登录状态")
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setGravity(Gravity.TOP)
                        .setShadow(false)
                        .setEnterAnimation(animation)

                )
                .playLater(textMoney);

        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("已用流量")
                        .setDescription("三张卡片都可以切换登录状态")
                        .setGravity(Gravity.TOP)
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setShadow(false)
                        .setEnterAnimation(animation)

                )
                .playLater(textFlow);

        ChainTourGuide tourGuide4 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("用户管理")
                        .setGravity(Gravity.TOP)
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setShadow(false)
                        .setEnterAnimation(animation)
                )
                .playLater(fabUsers);

        ChainTourGuide tourGuide5 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("后台管理")
                        .setGravity(Gravity.TOP)
                        .setShadow(false)
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setShadow(false)
                        .setEnterAnimation(animation)

                )
                .playLater(fabMore);

        ChainTourGuide tourGuide6 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setTitle("设置")
                        .setDescription("自动登录\n免流量直播")
                        .setBackgroundColor(Color.parseColor("#EB3349"))
                        .setGravity(Gravity.TOP)
                        .setShadow(false)
                        .setEnterAnimation(animation)

                )
                .setOverlay(new Overlay().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTourGuideHandler.next();
                        floatingActionsMenu.collapse();
                        SharedPreferences.Editor spEdit = getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
                        spEdit.putBoolean(FIRSTTIME, false);
                        spEdit.commit();
                        isFirstTime = false;
                        Toast.makeText(getApplicationContext(),"可以在\"设置\"中\n重新查看新手入门指导",Toast.LENGTH_LONG).show();
                        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);  //(普通消息框)
                        ab.setPositiveButton("立即加群!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3DOVbiu9aw_bqHtOgXM_fb17lOW0LpzKeA"));
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    // 未安装手Q或安装的版本不支持
                                }                            }
                        });
                        ab.setTitle("你好");
                        ab.setMessage("欢迎加群\n532607431\n获取更多使用校内应用");
                        AlertDialog dialog = ab.create();
                        dialog.show();

                    }
                }))
                .playLater(fabSetting);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide0, tourGuide1, tourGuide2, tourGuide3, tourGuide4, tourGuide5, tourGuide6)
                .setDefaultPointer(null)
                .setDefaultOverlay(new Overlay()
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })
                )
                .setContinueMethod(Sequence.ContinueMethod.OverlayListener)
                .build();

        mTourGuideHandler = ChainTourGuide.init(this).playInSequence(sequence);

    }

    private void initDialog() {
        dlg = new AlertDialog.Builder(MainActivity.this).create();
        dlg.setView(LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null));
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_edit_user);
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialogDelete = (Button) window.findViewById(R.id.dialog_delete);
        dialogEditTextPassword = (EditText) window.findViewById(R.id.dialog_editText_password);
        dialogButtonFind = (Button) window.findViewById(R.id.dialog_button_find);
        dialogButtonOk = (Button) window.findViewById(R.id.dialog_button_ok);
        dialogUsername = (TextView) window.findViewById(R.id.dialog_username);

        dialogButtonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                neuNetworkCenter.clearCookie();
                Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                intent.putExtra("user", editUser);
                startActivity(intent);
            }
        });

        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                String paswdStr = dialogEditTextPassword.getText().toString();
                if (paswdStr.equals("")) {
                    Toast.makeText(getApplicationContext(), "密码不应为空", Toast.LENGTH_SHORT).show();
                } else if (paswdStr.length() < 6) {
                    Toast.makeText(getApplicationContext(), "密码应该大于6位", Toast.LENGTH_SHORT).show();
                } else if (paswdStr.length() > 64) {
                    Toast.makeText(getApplicationContext(), "密码应该小于64位", Toast.LENGTH_SHORT).show();
                } else {
                    editUser.setPassword(paswdStr);
                    editUser.save(MainActivity.this);
                    getUserList();
                    adapterList.update(userList);
                    setRecentUser(editUser);
                    loginAndroid(currentUser);
                    hideUserList();
                    neuNetworkCenter.clearCookie();
                }
            }
        });

        dialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                editUser.delete(MainActivity.this);
                getUserList();
                adapterList.update(userList);
                currentUser = getRecentUser();
                loginAndroid(currentUser);
            }
        });
    }

    private void setRecentUser(User u) {
        currentUser = u;
        u.saveRecentLogin(MainActivity.this);
    }

    private User getRecentUser() {
        User user = User.getRecentLoginUser(MainActivity.this);
        if(user == null){
            if(userList.size() != 0){
                user = userList.get(0);
            }else{
                return null;
            }
        }
        return  user;
    }

    private void setOnlineColor() {
        cardLoginStatus.setColor("#00E0B0", "#00F3A0");
        statusImgBtn.setImageResource(R.drawable.open);
        getNewBlur();
    }

    private void setOfflineColor() {
        cardLoginStatus.setColor("#424242", "#757575");
        statusImgBtn.setImageResource(R.drawable.close);
        getNewBlur();
    }

    private void initUserList() {
        adapterList = new AdapterList(userList);
        LinearLayoutManager lm = new LinearLayoutManager(MainActivity.this);
        recyclerView_listUsers.setLayoutManager(lm);
        recyclerView_listUsers.setAdapter(adapterList);

        adapterList.setOnItemClickListener(new AdapterList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setRecentUser(userList.get(position));
                loginAndroid(currentUser);
                hideUserList();
                neuNetworkCenter.clearCookie();
            }

            @Override
            public void onItemLongClick(View view, int positon) {
                editUser = userList.get(positon);
                initDialog();
                dialogUsername.setText(editUser.getUsername());
                dialogEditTextPassword.setText(editUser.getPassword());
            }
        });

    }

    private void initData() {

        res = State.getStateMap();
        userList = User.loadAll(getBaseContext());
        currentUser = getRecentUser();
        neuNetworkCenter = new NeuNetworkCenter(MainActivity.this);
        getJianshuMessage = new GetJianshuMessage();


        if (userList.size() == 0) {
            addUser();
        }

        if (currentUser == null && userList.size() != 0) {
            currentUser = userList.get(0);
        }

        initUserList();
        if (currentUser == null) {
            textUsername.setText("NO\nACCOUNT");
        } else {
            textUsername.setText(currentUser.getUsername());
        }


        neuNetClient = new NeuNet(this);
        neuNetClient.setOnLoginExitStateListener(new NeuNet.OnLoginExitStateListener() {
            @Override
            public void getState(int state) {
                Log.e(TAG, "getState: "+res.get(state) );

                if (state == LOGIN_SUCCESS || state == PC_LOGIN_SUCCESS) {

                    //统计登录信息
                    HashMap<String, String> map = new HashMap<>();
                    map.put("user", currentUser.getUsername());
                    map.put("year", String.valueOf(currentUser.getYear()));
                    MobclickAgent.onEvent(MainActivity.this, "loginSuccess", map);

                    neuNetClient.getInfor();
                    if (Status != STATUS.ONLINE) {
                        setOnlineColor();
                    }
                    Status = STATUS.ONLINE;
                    cardLogin_text_status.setText(res.get(state));
                }else {
                    cardLogin_text_status.setText(res.get(state));

                    if (Status != STATUS.OFFLINE) {
                        setOfflineColor();
                    }
                    Status = STATUS.OFFLINE;
                    setFlow(null);
                    setMoney(null);
                    setIp(null);
                    waitingDone();

                }
                if(state == ALREADY_CONNECTED){
                    Snackbar.make(textFlow,"点击用户名下线所有设备\n点击第一张卡片Closed小图标模拟PC登录", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).setDuration(5000).show();
                }
                if(state == WRONG_USERNAME || state == WRONG_PASSWORD){
                    Snackbar.make(textFlow,"点击右下角进行用户管理", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        neuNetClient.setOnInforListener(new NeuNet.OnInforListener() {
            @Override
            public void getInfor(String flow, String money, String ip) {
                waitingDone();
                if (flow == null) {
                    if (Status == STATUS.OFFLINE) {
                        setOfflineColor();
                    } else {
                        setOfflineColor();
                        cardLogin_text_status.setText("OFFLINE");
                    }
                    Status = STATUS.OFFLINE;
                    if (isAutoLogin) {
                        isAutoLogin = false;
                        if (currentUser != null) {
                            loginAndroid(currentUser);
                        }
                    }
                } else {
                    if (Status != STATUS.ONLINE) {
                        setOnlineColor();
                        Status = STATUS.ONLINE;
                        cardLogin_text_status.setText(res.get(LOGIN_SUCCESS));
                        getJianshuMessage.getMessage();
                    }
                }
                setFlow(flow);
                setMoney(money);
                setIp(ip);
            }
        });

        getJianshuMessage.setOngetMessageListener(new GetJianshuMessage.onGetMessageListener() {
            @Override
            public void onMessage(String title, String url) {
                if (title != null) {
                    textMessage.setText(title);
                    MessageUrl = url;
                    textMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDownLoad(String title, String url) {
                textMessage.setText(title);
                textMessage.setVisibility(View.VISIBLE);
                MessageUrl = null;
                DownloadUrl = url;
            }
        });

    }

    private static final String TAG = "MainActivity";

    private void startWaiting() {
        pb.setVisibility(View.VISIBLE);
    }

    private void waitingDone() {
        pb.setVisibility(View.INVISIBLE);
    }

    public void loginAndroid(final User user) {
        if (user == null) {
            Toast.makeText(getApplicationContext(), "请先添加一个账号", Toast.LENGTH_SHORT).show();
            addUser();
            return;
        }
        cardLogin_text_status.setText("登录中...");
        user.saveRecentLogin(getBaseContext());
        startWaiting();
        textUsername.setText(user.getUsername());

        layoutRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                neuNetClient.login(user.getUsername(), user.getPassword(), "Android");
            }
        },800);
    }

    public void loginPc(final User user) {
        if (user == null) {
            Toast.makeText(getApplicationContext(), "请先添加一个账号", Toast.LENGTH_SHORT).show();
            addUser();
            return;
        }
        cardLogin_text_status.setText("登录中...");
        user.saveRecentLogin(getBaseContext());
        startWaiting();
        textUsername.setText(user.getUsername());

        layoutRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                neuNetClient.login(user.getUsername(), user.getPassword(), "Windows NT 10.0");
            }
        },800);

    }

    /**
     * 下线全部设备
     *
     * @param user 用户对象
     */
    public void exitAll(final User user) {
        if (user == null) {
            Toast.makeText(getApplicationContext(), "请先添加一个账号", Toast.LENGTH_SHORT).show();
            return;
        }
        cardLogin_text_status.setText("退出中...");
        startWaiting();
        user.saveRecentLogin(getBaseContext());
        layoutRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                neuNetClient.exit(user.getUsername(), user.getPassword(), true);
            }
        },800);
    }

    public void exit(final User user) {
        if (user == null) {
            Toast.makeText(getApplicationContext(), "请先添加一个账号", Toast.LENGTH_SHORT).show();
            return;
        }
        cardLogin_text_status.setText("退出中...");
        startWaiting();
        user.saveRecentLogin(getBaseContext());
        layoutRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                neuNetClient.exit(user.getUsername(), user.getPassword(), false);
            }
        },800);
    }

    private void addUser() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 100);
        hideUserList();
    }

    private void getUserList() {
        userList = User.loadAll(MainActivity.this);
        Collections.sort(userList);
    }

    private void setIp(String ip) {
        card_text_ip.setText(ip);
    }

    private void setFlow(String flow) {
        try {
            float f = Float.valueOf(flow);
            f/=1024;
            if(f >= 1){
                textFlow.setText(String.format(Locale.CHINA,"%.3f G",f));
            }else{
                textFlow.setText(String.format(Locale.CHINA,"%s M",flow));
            }
        }catch (Exception e){
            textFlow.setText(String.format(Locale.CHINA,"%s M",flow));
        }
    }

    private void setMoney(String money) {
        textMoney.setText("¥ " + money);
    }

    private void showUserList() {
        recyclerView_listUsers.setVisibility(View.VISIBLE);
        imageBtnAddUser.setVisibility(View.VISIBLE);
        textHint.setVisibility(View.VISIBLE);
        showBlur();
        getUserList();
        adapterList.update(userList);

        layoutRoot.setVisibility(View.INVISIBLE);
    }

    private void showBlur() {
        ImageBackground.setVisibility(View.VISIBLE);
    }

    private void hideUserList() {
        floatingActionsMenu.collapse();

        layoutRoot.setVisibility(View.VISIBLE);

        textHint.setVisibility(View.INVISIBLE);
        ImageBackground.setVisibility(View.INVISIBLE);
        imageBtnAddUser.setVisibility(View.INVISIBLE);
        recyclerView_listUsers.setVisibility(View.INVISIBLE);
    }

    public void hideUserList(View view) {
        hideUserList();
    }

    private void getNewBlur() {
        ImageBackground.setBackgroundColor(Color.WHITE);
//        ImageBackground.setImageDrawable(null);
//        try {
//            Blurry.with(MainActivity.this)
//                    .radius(10)
//                    .sampling(8)
//                    .color(Color.parseColor("#10000000"))
//                    .async()
//                    .capture(layoutRoot)
//                    .into(ImageBackground);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private boolean firstOpenToday = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (firstOpenToday) {
            firstOpenToday = false;
            getNewBlur();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
        if (!layoutRoot.isShown()) {
            hideUserList();
        } else {
            super.onBackPressed();
        }
    }

    public void cardLogin(View view) {

        if (currentUser != null) {
            if (Status == STATUS.OFFLINE) {
                loginAndroid(currentUser);
            } else if (Status == STATUS.ONLINE) {
                exit(currentUser);

            }
        }
    }

    public void openUrl(View view) {
        if (MessageUrl != null) {
            //统计打开网页次数
            HashMap<String, String> map = new HashMap<>();
            map.put("setting", "message");
            MobclickAgent.onEvent(getApplicationContext(), "web", map);

            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("url", MessageUrl);
            startActivity(intent);
        }else if(DownloadUrl != null){
            Uri uri = Uri.parse(DownloadUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        finish();
//    }

}
