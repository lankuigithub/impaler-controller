package com.shenchu.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shenchu.app.R;
import com.shenchu.app.listener.DataListener;
import com.shenchu.app.utils.DataUtils;
import com.shenchu.app.utils.MessageHandler;
import com.shenchu.app.utils.StringUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private EditText et_ip;
    private EditText et_port;
    private TextView tv_apply;

    private String mIpAddress;
    private String mPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        DataUtils.getInstance().initPreferences(mContext);
        initUI();
    }

    private void initUI() {
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_port = (EditText) findViewById(R.id.et_port);
        tv_apply = (TextView) findViewById(R.id.tv_apply);
        tv_apply.setOnClickListener(this);
        mIpAddress = DataUtils.getInstance().loadPreferences("ip");
        mPort = DataUtils.getInstance().loadPreferences("port");
        if (!StringUtils.isEmpty(mIpAddress)) {
            et_ip.setText(mIpAddress);
        }
        if (!StringUtils.isEmpty(mPort)) {
            et_port.setText(mPort);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_apply:
                mIpAddress = et_ip.getText().toString();
                mPort = et_port.getText().toString();
                if (StringUtils.isEmpty(mIpAddress) || StringUtils.isEmpty(mPort)) {
                    Toast.makeText(mContext, "IP地址或端口不能为空", Toast.LENGTH_LONG).show();
                }
                MessageHandler handler = MessageHandler.getInstance();
                handler.setDataListener(new DataListener() {
                    @Override
                    public void returnData(Object object) {
                        Intent intent = new Intent(mContext, ShowActivity.class);
                        startActivity(intent);
                    }
                });
                handler.connect(mIpAddress, Integer.valueOf(mPort));
                break;
            default:
                break;
        }
    }

}
