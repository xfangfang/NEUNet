package com.lalala.fangs.neunet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.lalala.fangs.data.User;
import com.lalala.fangs.data.table.FinancialCheckoutItem;
import com.lalala.fangs.data.table.FinancialPayItem;
import com.lalala.fangs.utils.FlowHelper;
import com.lalala.fangs.utils.NeuNetworkCenter;
import com.lalala.fangs.utils.State;
import com.lalala.fangs.view.Device;
import com.lalala.fangs.view.TableView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import static com.lalala.fangs.utils.State.CHANGE_PASSWORD_SUCCESS;
import static com.lalala.fangs.utils.State.CHANGE_USER_STATE_SUCCESS;
import static com.lalala.fangs.utils.State.LOGIN_SUCCESS;
import static com.lalala.fangs.utils.State.WRONG_USER_OR_PASSWORD;


public class MoreActivity extends AppCompatActivity {

    private User user;
    private NeuNetworkCenter neuNetworkCenterClient;
    private SparseArray<String> res;
    private boolean isPause = false;

    private TextView moreTextUsername;
    private LinearLayout moreActivityRootDevicesLayout;
    private Button dialogButtonCancle;
    private Button dialogButtonOk;
    private ImageView dialogImageVerifycode;
    private EditText dialogEditTextVerifycode;
    private AlertDialog dlg;
    private TableView tableviewMethod;
    private TextView textviewMethod;
    private TableView tableviewMethod2;
    private Button moreActivityButtonChangePassword;
    private EditText moreActivityEditViewOld;
    private EditText moreActivityEditViewNew1;
    private EditText moreActivityEditViewNew2;
    private Button moreActivityButtonPause;
    private LineChartView detailChartView;
    private TableView tableviewPayList;
    private ProgressBar progressBar;




// TODO: 2017/7/30 横竖屏切换不重载


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        initView();
        initData();

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

    private void initView() {
        moreTextUsername = (TextView) findViewById(R.id.more_text_username);
        moreActivityRootDevicesLayout = (LinearLayout) findViewById(R.id.more_activity_root_devices_layout);
        tableviewMethod = (TableView) findViewById(R.id.tableview_method);
        textviewMethod = (TextView) findViewById(R.id.textview_method);
        tableviewMethod2 = (TableView) findViewById(R.id.tableview_method2);
        moreActivityButtonChangePassword = (Button) findViewById(R.id.more_activity_button_change_password);
        moreActivityEditViewOld = (EditText) findViewById(R.id.more_activity_editView_old);
        moreActivityEditViewNew1 = (EditText) findViewById(R.id.more_activity_editView_new1);
        moreActivityEditViewNew2 = (EditText) findViewById(R.id.more_activity_editView_new2);
        moreActivityButtonPause = (Button) findViewById(R.id.more_activity_button_pause);
        detailChartView = (LineChartView) findViewById(R.id.detail_chartView);
        tableviewPayList = (TableView) findViewById(R.id.tableview_payList);
        progressBar = (ProgressBar) findViewById(R.id.more_activity_progressBar);



        moreTextUsername.setShadowLayer(10, 0, 0, Color.parseColor("#80555555"));
        Typeface typeface = Typeface.createFromAsset(MoreActivity.this.getAssets(), "BigJohnFang.ttf");
        moreTextUsername.setTypeface(typeface);

        moreActivityButtonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = moreActivityEditViewOld.getText().toString();
                String newPassword1 = moreActivityEditViewNew1.getText().toString();
                String newPassword2 = moreActivityEditViewNew2.getText().toString();
                clearDeviceLayout();
                neuNetworkCenterClient.getMoreInfor();
                neuNetworkCenterClient.changePassword(oldPassword, newPassword1, newPassword2);
            }
        });
        moreActivityButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDeviceLayout();
                neuNetworkCenterClient.getMoreInfor();
                neuNetworkCenterClient.pauseUser(!isPause);
            }
        });

        detailChartView.setViewportCalculationEnabled(false);
    }

    private void clearPasswordEditText() {
        moreActivityEditViewOld.setText("");
        moreActivityEditViewNew1.setText("");
        moreActivityEditViewNew2.setText("");
    }

    private static final String TAG = "MoreActivity";

    private LineChartData lineData;

    private void initData() {
        Intent intent = getIntent();
        // TODO: 2017/7/29 当从第三方启动时，读取sharedPreferences中的currentUser
        user = (User) intent.getSerializableExtra("user");
        moreTextUsername.setText(user.getUsername());
        res = State.getStateMap();

        neuNetworkCenterClient = new NeuNetworkCenter(MoreActivity.this, user);

        neuNetworkCenterClient.setOnUserStatusListener(new NeuNetworkCenter.OnUserStatus() {

            //登录的返回信息
            @Override
            public void stateChange(int code) {
                switch (code) {
                    case LOGIN_SUCCESS:
                        neuNetworkCenterClient.getFinancialCheckoutList();
                        neuNetworkCenterClient.getFinancialPayList();

                        //统计登录网络中心次数信息
                        HashMap<String, String> map = new HashMap<>();
                        map.put("user", user.getUsername());
                        MobclickAgent.onEvent(MoreActivity.this, "loginNeuNetworkCenterSuccess", map);
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case WRONG_USER_OR_PASSWORD:
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), res.get(code), Toast.LENGTH_SHORT).show();
                        finish();
                    default:
                        finish();
                        Toast.makeText(getApplicationContext(), res.get(code), Toast.LENGTH_SHORT).show();
                }
            }

            //修改密码的返回信息
            @Override
            public void changePasswordState(int code, String password) {
                Toast.makeText(getApplicationContext(), res.get(code), Toast.LENGTH_SHORT).show();
                switch (code) {
                    case CHANGE_PASSWORD_SUCCESS:
                        if (password != null) {
                            user.setPassword(password);
                            user.save(MoreActivity.this);

                            //统计修改密码次数
                            HashMap<String, String> map = new HashMap<>();
                            map.put("user", user.getUsername());
                            MobclickAgent.onEvent(MoreActivity.this, "changePassword", map);
                        }

                        clearDeviceLayout();
                        clearPasswordEditText();
                        neuNetworkCenterClient.getMoreInfor(true);
                        break;
                }
            }

            //取得在线的设备信息
            @Override
            public void getOnlineDevice(String user, String ip, String os, String time, String id) {
                addDevices(user, ip, os, time, id);

                //统计设备数目
                HashMap<String,String> map = new HashMap<>();
                map.put("os",os);
                MobclickAgent.onEvent(MoreActivity.this, "os",map);
            }

            //取套餐信息
            @Override
            public void getProductInformation(
                    String method, String usedFlow, String usedTime, String usedCounter
                    , String consume, String wallet) {
                method = method.replace("/", "\n");
                textviewMethod.setText(method);
                tableviewMethod.clearTableContents()
                        .setHeader("已用流量", "已用时长")
                        .addContent(usedFlow, usedTime.substring(0, 9))
                        .refreshTable();
                tableviewMethod2.clearTableContents()
                        .setHeader("使用次数", "消费金额", "钱包余额")
                        .addContent(usedCounter, consume, wallet)
                        .refreshTable();
            }

            //下线设备的返回信息
            @Override
            public void offline(boolean isSuccess, final View v) {
                if (isSuccess) {
                    moreActivityRootDevicesLayout.removeAllViews();
                    progressBar.setVisibility(View.VISIBLE);

                    neuNetworkCenterClient.getMoreInfor();

                    //统计下线设备次数
                    HashMap<String, String> map = new HashMap<>();
                    map.put("user", user.getUsername());
                    MobclickAgent.onEvent(MoreActivity.this, "turnOffDevice", map);

                } else {
                    Toast.makeText(getApplicationContext(), "下线失败", Toast.LENGTH_SHORT).show();
                    neuNetworkCenterClient.getMoreInfor(true);
                }
            }

            @Override
            public void getPauseInfor(boolean pause) {
                isPause = pause;
                setPauseButton(isPause);
            }

            @Override
            public void pauseState(int code) {
                if (code == CHANGE_USER_STATE_SUCCESS) {
                    Toast.makeText(getApplicationContext(), "更改用户状态成功", Toast.LENGTH_SHORT).show();
                    isPause = !isPause;
                    setPauseButton(isPause);

                    //统计修改用户状态
                    HashMap<String, String> map = new HashMap<>();
                    map.put("user", user.getUsername());
                    MobclickAgent.onEvent(MoreActivity.this, "changeUserState", map);

                } else {
                    Toast.makeText(getApplicationContext(), "更改用户状态失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        neuNetworkCenterClient.setOnLogListener(new NeuNetworkCenter.OnLogListener() {
            @Override
            public void onFinancialCheckoutLog(final ArrayList<FinancialCheckoutItem> financialCheckoutList) {

                List<AxisValue> axisValues = new ArrayList<>();
                List<PointValue> values = new ArrayList<>();
                int pointNum = financialCheckoutList.size();



                float maxFlux = 0;
                for (int i = 0; i < pointNum; i++) {
                    float value = FlowHelper.flowStr2FloatGb(financialCheckoutList.get(i).getFlux());
                    maxFlux  = maxFlux<value?value:maxFlux;
                    values.add(new PointValue(i, value));
                    String label = financialCheckoutList.get(i).getCreateTime().substring(5, 7);
                    axisValues.add(new AxisValue(i).setLabel(label));
                }
                String start="",end="";
                if(financialCheckoutList.size() > 0){
                    start=financialCheckoutList.get(0).getCreateTime().substring(2,7);
                    end = financialCheckoutList.get(financialCheckoutList.size()-1).getCreateTime().substring(2,7);
                }


                Viewport v = new Viewport(0, maxFlux+3, pointNum, 0);
                Viewport vv = new Viewport(0, maxFlux+3, 7, 0);
                detailChartView.setMaximumViewport(v);
                detailChartView.setCurrentViewport(vv);
                detailChartView.setZoomType(ZoomType.HORIZONTAL);

                Line line = new Line(values);
                line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

                List<Line> lines = new ArrayList<>();
                lines.add(line);

                lineData = new LineChartData(lines);
                lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true).setName("时间 ("+start+" ~ "+end+")"));
                lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3).setName("月流量 (G)"));

                detailChartView.setLineChartData(lineData);
                detailChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                    @Override
                    public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                        FinancialCheckoutItem i = financialCheckoutList.get(pointIndex);
                        String pointLabel = i.getPackages().replaceAll("-","");
                        String mess;
                        if(pointLabel.isEmpty()){
                            mess = "结算信息\n" + i.getCreateTime() + "\n流量\n" + i.getFlux();
                        }else{
                            mess = "结算信息\n" + i.getCreateTime() + "\n流量\n" + i.getFlux()+"\n"+pointLabel;
                        }
                        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onValueDeselected() {
                        Log.e(TAG, "onValueDeselected: 取消选择");
                    }
                });
            }

            @Override
            public void onFinancialPayLog(ArrayList<FinancialPayItem> financialPayList) {
                tableviewPayList.clearTableContents()
                        .setHeader("缴费时间","缴费金额")
                        .setColumnWeights(2,1);
                for(FinancialPayItem i:financialPayList){
                    tableviewPayList.addContent(i.getCreateTime(), i.getPayNum());
                }
                tableviewPayList.refreshTable();
            }
        });

        neuNetworkCenterClient.getMoreInfor();
    }

    private void setPauseButton(boolean isPause) {
        if (isPause) {
            moreActivityButtonPause.setText("开启");
        } else {
            moreActivityButtonPause.setText("暂停");
        }
    }

    private void clearDeviceLayout() {
        moreActivityRootDevicesLayout.removeAllViews();
    }

    private void addDevices(String user, String ip, String os, String time, String id) {

        Device one = new Device(this, id);
        one.setIp(ip);
        one.setTime(time);
        one.setOS(os);
        moreActivityRootDevicesLayout.addView(one);

        one.setOnOfflineClickedListener(new Device.onOffLineClickedListener() {
            @Override
            public void offLine(View v, String id) {
                neuNetworkCenterClient.dropDevice(id, v);
                // TODO: 2017/7/30 返回主窗口时，刷新在线信息 
            }
        });

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
