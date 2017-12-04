package com.shenchu.app.utils;

/**
 * Created by admin on 16/12/14.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.shenchu.app.listener.DataListener;
import com.shenchu.app.vo.RecvData;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class MessageHandler {
    private final String TAG = getClass().getSimpleName();
    private static MessageHandler mInstance;
    private boolean isRun = true;
    private Socket mSocket;
    private Timer mTimer;
    private String mIpAddress;
    private int mPort;
    private DataListener mDataListener;
    private Context mContext;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private int mDeviceId = 0;

    private final static int HEART_BEAT_SLEEP = 20000;

    private final static String END = "IMPALER";
    private final static String PING = "";
    private final static int BUFFER_MAX_LENGTH = 1024;
    private final static int INTEGER_LENGTH = 4;

    public final static int COMMAND_HEART_BEAT = 0x00000001;
    public final static int COMMAND_MESSAGE = 0x00000002;
    public final static int COMMAND_IMAGE = 0x00000003;
    public final static int COMMAND_SCREEN = 0x00000004;
    public final static int COMMAND_CAMERA = 0x00000005;
    public final static int COMMAND_REGISTER = 0x00000006;
    public final static int COMMAND_CLIENT_LIST = 0x00000007;

    private MessageHandler() {
    }

    public static MessageHandler getInstance() {
        if (mInstance == null) {
            mInstance = new MessageHandler();
        }
        return mInstance;
    }

    //设置消息监听
    public void setDataListener(DataListener listener) {
        mDataListener = listener;
    }

    //设置上下文
    public void setContext(Context context) {
        mContext = context;
    }

    public void connect(final String ip, final int port) {
        isRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 1、创建客户端Socket，指定服务器地址和端口
                    mSocket = new Socket(ip, port);
                    mInputStream = mSocket.getInputStream();
                    mOutputStream = mSocket.getOutputStream();
                    Log.d(TAG, "客户端连接成功");
                    mIpAddress = ip;
                    mPort = port;
                    DataUtils.getInstance().savePreferences("ip", ip);
                    DataUtils.getInstance().savePreferences("port", port);
                    mUIHandler.sendEmptyMessage(0);
                    //while (isRun && mSocket.isConnected()) {
                    recv();
                    //}
                    mSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();// 出错，打印出错信息
                    disconnect();
                }
            }
        }).start();
    }

    public void disconnect() {
        try {
            isRun = false;
            mTimer.cancel();
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
            Log.d(TAG, "客户端断开连接");
        } catch (Exception e) {
            e.printStackTrace();// 出错，打印出错信息
        }
    }

    private void recv() {
        try {
            //3、获取输入流，并读取服务器端的响应信息
            if (null != mInputStream) {
                synchronized (mInputStream) {
                    int len = -1;
                    byte buffer[] = new byte[BUFFER_MAX_LENGTH];
                    byte command[] = new byte[INTEGER_LENGTH];
                    byte id[] = new byte[INTEGER_LENGTH];
                    byte length[] = new byte[INTEGER_LENGTH];
                    byte data[] = null;
                    int endLength = END.getBytes("utf-8").length;
                    int dataLength = 0;
                    int recvLength = 0;
                    int index = 0;
                    RecvData recvData = new RecvData();
                    //FileOutputStream fileOutputStream = null;
                    //byte end[] = new byte[endLength];
                    while ((len = mInputStream.read(buffer)) != -1 && isRun) {
                        int first = 0;
                        if (0 == recvData.getCommand()) {
                            System.arraycopy(buffer, 0, command, 0, 4);
                            recvData.setCommand(byteToInt(command));
                            System.arraycopy(buffer, 4, id, 0, 4);
                            recvData.setId(byteToInt(id));
                            System.arraycopy(buffer, 8, length, 0, 4);
                            recvData.setLength(byteToInt(length));
                            dataLength = recvData.getLength();//数据长度
                            if (dataLength > 0) {
                                data = new byte[recvData.getLength()];
                            }
                            first = 12;
                            len -= 12;
                            recvLength = 0;
                            index = 0;
                            //String uri = Environment.getExternalStorageDirectory().getPath();
                            //fileOutputStream = new FileOutputStream(uri + "/mlmg/1.jpg");
                        }
                        //Log.d(TAG, recvData.getCommand() + "-----------" + dataLength);
                        if (dataLength > 0) {
                            if (recvLength - endLength < dataLength) {
                                int left = len;
                                if (len < BUFFER_MAX_LENGTH) {
                                    left -= endLength;
                                }
                                System.arraycopy(buffer, first, data, index, left);
                                index += len;//每次增加len
                                recvLength += len;
                                //fileOutputStream.write(buffer, 0, len);
                            }
//                            Log.d(TAG, recvData.getCommand() + "recv: " + recvLength);
//                            Log.d(TAG, recvData.getCommand() + "data: " + dataLength);
                            if (recvLength - endLength == dataLength) {
                                recvData.setData(data);
                                Message message = new Message();
                                message.obj = recvData;
                                mHandler.sendMessage(message);
                                recvData = new RecvData();
                                //fileOutputStream.flush();
                                //fileOutputStream.close();
                            }
                        } else {
                            Message message = new Message();
                            message.obj = recvData;
                            mHandler.sendMessage(message);
                            recvData = new RecvData();
                        }
                    }
                }
            }
        } catch (Exception e) {
            disconnect();
            e.printStackTrace();
        }
    }

    public void send(final int command, final String content) {
        if (!isRun) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //根据输入输出流和服务端连接
                    synchronized (mOutputStream) {
                        byte[] data = content.getBytes("utf-8");
                        int length = data.length;
                        mOutputStream.write(intToBytes(command));
                        mOutputStream.write(intToBytes(mDeviceId));
                        mOutputStream.write(intToBytes(length));
                        //if (length > 0) {
                        mOutputStream.write(data);
                        //}
                        mOutputStream.write(END.getBytes("utf-8"));
                        mOutputStream.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();// 出错，打印出错信息
                }
            }
        }).start();
    }

    //收到数据包
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != msg.obj) {
                String message = msg.obj.toString();
                RecvData recvData = (RecvData) msg.obj;
                switch (recvData.getCommand()) {
                    case COMMAND_HEART_BEAT:
                        Log.d(TAG, "handleMessage: 收到心跳包");
                        break;
                    case COMMAND_REGISTER:
                        Log.d(TAG, "handleMessage: 注册成功");
                        mDeviceId = byteToInt(recvData.getData());
                        send(COMMAND_CLIENT_LIST, "");
                        break;
                    case COMMAND_CLIENT_LIST:
                        Log.d(TAG, "handleMessage: 列表请求成功" + new String(recvData.getData()));
                        break;
                    case COMMAND_IMAGE:
                    case COMMAND_SCREEN:
                    case COMMAND_CAMERA:
                        if (null != mContext) {
                            Toast.makeText(mContext, "Command:" + recvData.getCommand(), Toast.LENGTH_LONG).show();
                        }
//                        recvData.setCommand(COMMAND_IMAGE);
//                        recvData.setData(Drawable2Bytes(mContext.getResources().getDrawable(R.mipmap.ic_launcher)));
//                        Log.d(TAG, "handleMessage: 显示图片");
                        if (null != mDataListener) {
                            byte data[] = recvData.getData();
                            if (null != data && data.length > 0) {
                                mDataListener.returnData(recvData);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    //连接成功，刷新界面
    Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            send(COMMAND_REGISTER, "");
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    send(COMMAND_HEART_BEAT, PING);
                }
            }, HEART_BEAT_SLEEP, HEART_BEAT_SLEEP);//心跳包20秒发一次
            mDataListener.returnData(0);
        }
    };

    //int转byte数组
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];

        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));

        }
        return b;
    }

    //byte转换为int
    public static int byteToInt(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }
}
