package com.lalala.fangs.neunet;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lalala.fangs.utils.ShortcutReceiver;
import com.umeng.analytics.MobclickAgent;

import java.net.URISyntaxException;
import java.util.HashMap;

public class AboutActivity extends AppCompatActivity{
    private TextView aboutActivityText;

    private SwitchCompat switchAutoLogin;
    private SwitchCompat switchPcLogin;

    private boolean isAutoLogin;
    private boolean isPCLogin;
    private final String SETTING="SETTING";
    private String FIRSTTIME = "firstTime3.3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#9e9e9e"));
        }

        //统计打开设置
        MobclickAgent.onEvent(AboutActivity.this, "setting");
        initView();
        loadData();
        setView();

    }

    private void initView(){
        aboutActivityText = (TextView) findViewById(R.id.aboutActivity_text);
        switchAutoLogin = (SwitchCompat) findViewById(R.id.switch_autoLogin);
        switchPcLogin = (SwitchCompat) findViewById(R.id.switch_pcLogin);
        aboutActivityText.setShadowLayer(10, 0, 0, Color.parseColor("#80555555"));

        switchPcLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPCLogin = isChecked;
                saveData();

                if(isPCLogin){
                    HashMap<String,String> map = new HashMap<>();
                    map.put("setting","pcLoginInc");
                    MobclickAgent.onEvent(getApplicationContext(), "web",map);
                }else{
                    HashMap<String,String> map = new HashMap<>();
                    map.put("setting","pcLoginDec");
                    MobclickAgent.onEvent(getApplicationContext(), "web",map);
                }
            }
        });

        switchAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoLogin = isChecked;
                saveData();

                if(isAutoLogin){
                    HashMap<String,String> map = new HashMap<>();
                    map.put("setting","autoLoginInc");
                    MobclickAgent.onEvent(getApplicationContext(), "web",map);
                }else{
                    HashMap<String,String> map = new HashMap<>();
                    map.put("setting","autoLoginDec");
                    MobclickAgent.onEvent(getApplicationContext(), "web",map);
                }
            }
        });
    }

    private void loadData(){
        SharedPreferences sp = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        isPCLogin = sp.getBoolean("pcLogin2",true);
        isAutoLogin = sp.getBoolean("autoLogin",false);
    }

    private void setView(){
        switchPcLogin.setChecked(isPCLogin);
        switchAutoLogin.setChecked(isAutoLogin);
    }

    private static final String TAG = "AboutActivity";

    private void saveData(){

        SharedPreferences.Editor spEdit = getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();

        spEdit.putBoolean("pcLogin2",isPCLogin);
        spEdit.putBoolean("autoLogin",isAutoLogin);

        spEdit.commit();
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

    public void addQQGroup(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","qq");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);

        joinQQGroup("OVbiu9aw_bqHtOgXM_fb17lOW0LpzKeA");
    }

    public void version(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","version");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);

        Intent intent = new Intent(AboutActivity.this, WebActivity.class);
        intent.putExtra("url","http://www.jianshu.com/p/0c51f0864ca5");
        startActivity(intent);
    }

    public void mail(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","mail");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT,"网关君反馈");
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"include_fang.h@qq.com"});
        intent.putExtra(Intent.EXTRA_TEXT,"为网关君提建议：");
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "看起来你发不了邮件...", Toast.LENGTH_SHORT).show();
        }
    }

    public void help(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","help");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);


        Intent intent = new Intent(AboutActivity.this, WebActivity.class);
        intent.putExtra("url","http://www.jianshu.com/p/5bdbee749fde");
        startActivity(intent);
    }

    public void secrect(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","secrect");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);


        Intent intent = new Intent(AboutActivity.this, WebActivity.class);
        intent.putExtra("url","http://www.jianshu.com/p/61704561cec7");
        startActivity(intent);
    }

    public void thirdParty(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","thirdParty");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);

        Intent intent = new Intent(AboutActivity.this, WebActivity.class);
        intent.putExtra("url","http://www.jianshu.com/p/b1f65414db0a");
        startActivity(intent);
    }

    public void openTV(View view){
        //统计打开网页次数
        HashMap<String,String> map = new HashMap<>();
        map.put("setting","tv");
        MobclickAgent.onEvent(getApplicationContext(), "web",map);

        String packageName = "com.lalala.fangs.neutv";
        Intent intent = this.getPackageManager().getLaunchIntentForPackage(packageName);

        try {
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"请先安装直视",Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://xfangfang.github.io/NeuTV/");
            intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public void autoLogin(View view){

        switchAutoLogin.setChecked(!isAutoLogin);
    }

    public void pcLogin(View view){
        switchPcLogin.setChecked(!isPCLogin);
    }

    public void guide(View view){
        SharedPreferences.Editor spEdit = getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        spEdit.putBoolean(FIRSTTIME, true);
        spEdit.commit();
        finish();
    }

    /****************
     *
     * 发起添加群流程。群号：直视 官方BUG反馈(532607431) 的 key 为： OVbiu9aw_bqHtOgXM_fb17lOW0LpzKeA
     * 调用 joinQQGroup(OVbiu9aw_bqHtOgXM_fb17lOW0LpzKeA) 即可发起手Q客户端申请加群 直视 官方BUG反馈(532607431)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void addShortcut(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Context context = AboutActivity.this;
            ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
            if (shortcutManager.isRequestPinShortcutSupported()) {
                Intent launcherIntent = new Intent(context, FloatActivity.class);
                launcherIntent.setAction(Intent.ACTION_MAIN);
                launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                ShortcutInfo info = new ShortcutInfo.Builder(context, "neunet-login")
                        .setIcon(Icon.createWithResource(context,R.drawable.ic_login))
                        .setShortLabel( getResources().getString(R.string.sign_in))
                        .setIntent(launcherIntent)
                        .build();
                //当添加快捷方式的确认弹框弹出来时，将被回调
                PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ShortcutReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());

            }

        }
        else {

            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            addShortcutIntent.putExtra("duplicate", false);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.sign_in));
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.drawable.ic_login));
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.setClass(getApplicationContext(), FloatActivity.class);
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            addShortcutIntent
                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
            sendBroadcast(addShortcutIntent);

            addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            addShortcutIntent.putExtra("duplicate", false);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.sign_out));
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.drawable.ic_logoff));
            launcherIntent = new Intent(Intent.ACTION_MAIN);
            launcherIntent.setClass(getApplicationContext(), FloatExitActivity.class);
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            addShortcutIntent
                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
            sendBroadcast(addShortcutIntent);


            Toast.makeText(getApplicationContext(), "去桌面看看添加了没有？", Toast.LENGTH_SHORT).show();
        }
    }


    public void openGithub(View view) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("https://github.com/xfangfang/NEUNet"));
        intent.setAction(Intent.ACTION_VIEW);
        this.startActivity(intent);
    }


    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     * @return 支付宝客户端是否已安装
     */
    public boolean hasInstalledAlipayClient() {
        String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 支付宝转账
     * **/
    public void openALiPay(View view){
        String url1="intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fa6x05115dwlymstrxftrhb3%3F_s" +
                "%3Dweb-other#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
        Intent intent = null;
        Toast.makeText(getApplicationContext(),"感谢您的心意！🥰",Toast.LENGTH_SHORT).show();
        if(hasInstalledAlipayClient()){
            try {
                intent = Intent.parseUri(url1 ,Intent.URI_INTENT_SCHEME );
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"捐赠失败",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"未安装支付宝",Toast.LENGTH_SHORT).show();
        }
    }
}
