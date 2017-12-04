package com.shenchu.app.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shenchu.app.R;
import com.shenchu.app.adapter.GalleryAdapter;
import com.shenchu.app.listener.DataListener;
import com.shenchu.app.utils.DataUtils;
import com.shenchu.app.utils.MessageHandler;
import com.shenchu.app.utils.StringUtils;
import com.shenchu.app.vo.Message;
import com.shenchu.app.vo.RecvData;
import com.shenchu.app.vo.ViewHolder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private EditText et_content;
    private TextView tv_apply;
    private TextView tv_title;
    private TextView tv_return;
    private GalleryAdapter mListAdapter;
    private List<Message> mList;
    private List<Integer> mLayoutList;
    private RecyclerView rv_recyclerview;

    private String mIpAddress;
    private String mPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        mContext = this;
        initUI();
    }

    @Override
    public void onBackPressed() {
    }

    private void initUI() {
        et_content = (EditText) findViewById(R.id.et_content);
        tv_apply = (TextView) findViewById(R.id.tv_apply);
        tv_apply.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_return = (TextView) findViewById(R.id.tv_return);
        tv_return.setOnClickListener(this);
        mIpAddress = DataUtils.getInstance().loadPreferences("ip");
        mPort = DataUtils.getInstance().loadPreferences("port");
        tv_title.setText("已连接到" + mIpAddress + ":" + mPort);
        mList = new ArrayList<>();
        mLayoutList = new ArrayList<>();
        mLayoutList.add(R.layout.item_message_self);
        mLayoutList.add(R.layout.item_message_other);
        rv_recyclerview = (RecyclerView) findViewById(R.id.rv_recyclerview);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_recyclerview.setLayoutManager(linearLayoutManager);
        //设置适配器
        mListAdapter = new GalleryAdapter<Message>(mContext, mList, mLayoutList) {
            @Override
            public void convert(ViewHolder holder, Message item, int position, int type) {
                holder.setText(R.id.tv_name, item.getName());
                if (item.getCommand() == MessageHandler.COMMAND_IMAGE) {
                    holder.setVisibility(R.id.iv_image, View.VISIBLE);
                    holder.setVisibility(R.id.tv_content, View.GONE);
                    holder.setImageByBitmap(R.id.iv_image, BitmapFactory.decodeByteArray(item.getContent(), 0, item.getContent().length));
                } else {
                    holder.setVisibility(R.id.iv_image, View.GONE);
                    holder.setVisibility(R.id.tv_content, View.VISIBLE);
                    String content = new String(item.getContent());
                    holder.setText(R.id.tv_content, content);
                    TextView tv_content = holder.getView(R.id.tv_content);
                    int width = (int) tv_content.getPaint().measureText(content, 0, content.length());//计算字体空隙
                    tv_content.setWidth(width);
                }
                //Rect rect = new Rect();
                //tv_content.getPaint().getTextBounds(item.getContent(), 0, item.getContent().length(), rect);
                //int width = rect.width();
            }
        };
        rv_recyclerview.setAdapter(mListAdapter);
        MessageHandler.getInstance().setContext(mContext);
        MessageHandler.getInstance().setDataListener(mDataListener);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_apply:
                try {
                    String content = et_content.getText().toString();
                    if (StringUtils.isEmpty(content)) {
                        Toast.makeText(mContext, "请输入发送内容", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Message message = new Message();
                    message.setName("客户端");
                    message.setContent(content.getBytes("utf-8"));
                    message.setTime(System.currentTimeMillis() / 1000);
                    message.setType(0);
                    message.setLayoutType(0);
                    mList.add(message);
                    mListAdapter.notifyData(mList);
                    rv_recyclerview.smoothScrollToPosition(mList.size());
                    MessageHandler.getInstance().send(2, content);
                    et_content.setText("");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_return:
                MessageHandler.getInstance().disconnect();
                finish();
                break;
            default:
                break;
        }
    }

    DataListener mDataListener = new DataListener() {
        @Override
        public void returnData(Object object) {
            RecvData recvData = (RecvData) object;
            Message message = new Message();
            message.setCommand(recvData.getCommand());
            message.setName("服务器");
            message.setContent(recvData.getData());
            message.setTime(System.currentTimeMillis() / 1000);
            message.setType(1);
            message.setLayoutType(1);
            mList.add(message);
            mListAdapter.notifyData(mList);
            rv_recyclerview.smoothScrollToPosition(mList.size());
            //Log.d(TAG, "returnData: " + object.toString());
        }
    };
}
