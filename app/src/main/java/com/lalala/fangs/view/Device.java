package com.lalala.fangs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lalala.fangs.neunet.R;


public class Device extends FrameLayout {
    private Button btn_offline;
    private ImageView img_os;
    private TextView text_ip,text_time;
    private String id;


    public Device(Context context) {
        super(context);
        init(null, 0,context);
    }

    public Device(Context context, String id) {
        super(context);
        this.id = id;
        init(null, 0,context);
    }

    public Device(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0,context);
    }

    public Device(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle,context);
    }

    private static final String TAG = "device";
    private void init(AttributeSet attrs, int defStyle, final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.card_device,this);
        btn_offline = (Button) view.findViewById(R.id.card_online_btn_offline);
        img_os = (ImageView) view.findViewById(R.id.card_online_device);
        text_time = (TextView) view.findViewById(R.id.card_online_text_time);
        text_ip = (TextView) view.findViewById(R.id.card_online_text_ip);

        btn_offline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offLineListener != null){
                    offLineListener.offLine(view,id);
                }
            }
        });

    }



    public void setIp(String ip){
        this.text_ip.setText(ip);
    }

    public void setTime(String time){
        this.text_time.setText(time);
    }

    public void setOS(String os){
        os = os.toLowerCase();
        if(os.equals("unknown")){
            img_os.setImageResource(R.mipmap.ic_unknown);
        }else if(os.contains("phone")){
            img_os.setImageResource(R.mipmap.ic_mobile);
        }else if(os.contains("win") || os.contains("mac") || os.contains("linux")){
            img_os.setImageResource(R.mipmap.ic_pc);
        }else{
            img_os.setImageResource(R.mipmap.ic_mobile);
        }
    }

    public interface onOffLineClickedListener{
        void offLine(View v,String id);
    }

    private onOffLineClickedListener offLineListener;

    public void setOnOfflineClickedListener(onOffLineClickedListener lintener){
        this.offLineListener = lintener;
    }

}




