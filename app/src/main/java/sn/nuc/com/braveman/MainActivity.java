package sn.nuc.com.braveman;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends CheckPermissionsActivity {

    private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄

    private View btn;
    private Button setbtn;
    private EditText edt1, edt2, edt3;
    private PopupWindow popupWindow;
    private View popupView;
    private TranslateAnimation animation;
    private boolean isStart = false;
    private MediaRecorder mr = null;
    String emphone1,emphone2,emmail,mailfrom,mailcode,mailTarget1,mailTarget2,name;
    private static final String TAG = "GpsActivity";
    private LocationManager locationManager;
    private double latitude;
    private double longitude;
    private String filepath;
    private Integer flag = 0;
    private String zippath;
    private Switch bluesw, warnsw;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mhandler;
    private BleDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    public static String bleAddress;
    int rssi;
    private static final long SCAN_PERIOD = 10000;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<BluetoothGattService> gattServices = new ArrayList<BluetoothGattService>();
    private boolean isblue;
    int resultLengthNum;
    String result;
    String text_string = null;
    String resultLength = null;
    boolean iswarning = false;
    boolean iswarning2 = false;
    boolean iswarning3 = false;
    boolean istrue = false;

    BleService bleService;

    public static final UUID RX_ALART_UUID = UUID
            .fromString("00001802-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID
            .fromString("0000ffe0-0000-1000-8000-00805f9b34fb");// DE5BF728-D711-4E47-AF26-65E3012A5DC7
    public static final UUID MY_SERVICE_UUID = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID MY_CHAR_UUID = UUID
            .fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID
            .fromString("00002A06-0000-1000-8000-00805f9b34fb");// DE5BF729-D711-4E47-AF26-65E3012A5DC7
    public static final UUID TX_CHAR_UUID = UUID
            .fromString("0000ffe1-0000-1000-8000-00805f9b34fb");// DE5BF72A-D711-4E47-AF26-65E3012A5DC7
    public static final UUID CCCD = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID C22D = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_SERVICE_UUID = UUID
            .fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_CHAR_UUID = UUID
            .fromString("00002a19-0000-1000-8000-00805f9b34fb");

    private final ServiceConnection conn = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            bleService = ((BleService.LocalBinder) service).getService();
            if (!bleService.init()) {
                finish();
                Log.e(TAG ,"连接失败！");

            }
            bleService.connect(bleAddress);
            isblue = true;
            Log.e(TAG ,"连接成功！");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            bleService = null;
        }
    };

    BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint({ "NewApi", "DefaultLocale" })
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "设备连接成功！",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (!isblue)
                    Toast.makeText(MainActivity.this, "设备断开！", Toast.LENGTH_SHORT)
                            .show();
            }
            if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String uuid = null;
                bleService.mBluetoothGatt.readRemoteRssi();
                gattServices = bleService.mBluetoothGatt.getServices();
                BluetoothGattService service = bleService.mBluetoothGatt.getService(RX_SERVICE_UUID);
                final BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                bleService.mBluetoothGatt.readCharacteristic(characteristic);
//                runOnUiThread(new Runnable() {
//                    @SuppressLint("NewApi")
//                    public void run() {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        characteristic, true);
            }
            if (BleService.ACTION_CHAR_READED.equals(action)) {
                final String des1String = intent.getExtras().getString(
                        "desriptor1");
                final String des2String = intent.getExtras().getString(
                        "desriptor2");
                final String stringValue = intent.getExtras().getString(
                        "StringValue");
                final String hexValue = intent.getExtras()
                        .getString("HexValue");
                final String readTime = intent.getExtras().getString("time");
            }
            if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
                result = intent.getExtras().getString(
                        BleService.EXTRA_STRING_DATA);
                String i = result.substring(0, 1);
                if (i.equals("+"))
                {
                    Toast.makeText(MainActivity.this,"安全！",Toast.LENGTH_SHORT).show();
                    iswarning = false;
                    iswarning2 = false;
                    iswarning3 = false;
                }
                else if(i.equals("!"))
                {
                    if(!iswarning)
                    {
                        Toast.makeText(MainActivity.this,"一次点击！",Toast.LENGTH_SHORT).show();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getLocation();
                                sendMsg();
                            }
                        });
                        thread.start();
                        iswarning = true;
                    }
                }
                else if(i.equals("@"))
                {
                    if(!iswarning2)
                    {
                        Toast.makeText(MainActivity.this,"二次点击！",Toast.LENGTH_SHORT).show();
                        if (!isStart) {
                            startRecord();
                            Toast.makeText(MainActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
                            isStart = true;
                        }
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(20*1000);
                                }catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                                stopRecord();
                                Log.e("filename", filepath);
                                filepath = filepath + "/" + name + ".amr";
                                Log.e("filename", filepath);
                                String[] filePath = {filepath};
                                ZipUtil.zip(zippath, filePath);
                                sendCSVFilByJavaMail();
                            }
                        });
                        thread.start();
                        iswarning2 = true;
                    }
                }
                else if(i.equals("#"))
                {
                    if(!iswarning3)
                    {
                        Toast.makeText(MainActivity.this,"三次点击！",Toast.LENGTH_SHORT).show();
                        Intent mintent;
                        if(istrue)
                        {
                            mintent = new Intent(MainActivity.this, MapActivity.class);
                        }
                        else
                        {
                            mintent = new Intent(MainActivity.this, Map2Activity.class);
                        }
                        startActivity(mintent);
                        iswarning3 = true;
                    }
                }
                int countNumber = intent.getExtras().getInt(
                        BleService.EXTRA_DATA_LENGTH);
                text_string = result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Log.e(TAG, text_string);
                    }
                });
            }
            if (BleService.ACTION_GATT_RSSI.equals(action)) {
                rssi = intent.getExtras().getInt(BleService.EXTRA_DATA_RSSI);
                MainActivity.this.invalidateOptionsMenu();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn_1);
        setbtn = findViewById(R.id.settingbtn);
        bluesw = findViewById(R.id.bluet);
        warnsw = findViewById(R.id.warnin);

        isblue = false;

        getBleAdapter();

        warnsw.setChecked(false);
        warnsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    istrue = true;
                } else {
                    istrue = false;
                }
            }
        });


        bluesw.setChecked(false);
        bluesw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Intent serverIntent = new Intent(MainActivity.this, DeviceScanActivity.class); //跳转程序设置
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
                } else {
                    if (mBluetoothAdapter != null || mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                        if(isblue)
                            Toast.makeText(MainActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        zippath = Environment.getExternalStorageDirectory().getPath() + "/" + "zipsoundss" + "/" + "紧急录音.zip";
        Intent intent = getIntent();
        emphone1 = intent.getStringExtra("phonenum");
        emmail = intent.getStringExtra("mailto");
        Log.e("MAIN", emphone1);
        Log.e("MAIN",emmail);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        btn.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
            @Override
            public void oneClick() {
                Toast.makeText(MainActivity.this, "点击一次", Toast.LENGTH_SHORT).show();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getLocation();
                        sendMsg();
                    }
                });
                thread.start();
                //String url = "http://127.0.0.1:8000/serach";//替换成自己的服务器地址
                //SendMessage(url, name);
            }

            @Override
            public void doubleClick() {
                Toast.makeText(MainActivity.this, "点击两次", Toast.LENGTH_SHORT).show();
                if (!isStart) {
                    startRecord();
                    Toast.makeText(MainActivity.this, "开始录音", Toast.LENGTH_SHORT).show();
                    isStart = true;
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(20*1000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        stopRecord();
                        Log.e("filename", filepath);
                        filepath = filepath + "/" + name + ".amr";
                        Log.e("filename", filepath);
                        String[] filePath = {filepath};
                        ZipUtil.zip(zippath, filePath);
                        sendCSVFilByJavaMail();
                    }
                });
                thread.start();
            }
            @Override
            public void thirdClick() {
                Toast.makeText(MainActivity.this, "点击三次", Toast.LENGTH_LONG).show();
                Intent intent;
                if(istrue)
                {
                    intent = new Intent(MainActivity.this, MapActivity.class);
                }
                else
                {
                    intent = new Intent(MainActivity.this, Map2Activity.class);
                }
                startActivity(intent);
            }
        }));

        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });

    }

    private void sendMsg() {
        Random ra = new Random();
        String content = new String();
        //String number = mNumberText.getText().toString();
        //String content = mContentText.getText().toString();
        if (flag == 0) {
            content = ("我遇到了紧急情况，可能会有生命危险。我的坐标是纬度：" + latitude + "\n经度" + longitude + "\",收到消息及时报警！");
            flag = 1;
            Log.e("Context", "短信内容得到");
        } else {
            flag = 0;
        }
        try {
            if (TextUtils.isEmpty(content)) {
                return;
            }
            if(TextUtils.isEmpty(emphone1))
            {
                if(TextUtils.isEmpty(emphone2))
                {
                    return;
                }
                SmsManager.getDefault().sendTextMessage(emphone2, null, content, null, null);
            }
            else{
                SmsManager.getDefault().sendTextMessage(emphone1, null, content, null, null);
                Log.e("短信","短信已发送");
                if(TextUtils.isEmpty(emphone2))
                {
                    return;
                }
                SmsManager.getDefault().sendTextMessage(emphone2, null, content, null, null);
            }

           /* ArrayList<String> messages = SmsManager.getDefault().divideMessage(content);
            for (String text : messages) {
                SmsManager.getDefault().sendTextMessage(number, null, text, null, null);
            }*/
            //Log.d("MainActivity", "1");
        } catch (SecurityException e) {
            Log.e("","error");
        }}



    private void pop() {
        if (popupWindow == null) {
            popupView = View.inflate(this, R.layout.settingwindow, null);
            // 参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    lighton();
                }
            });

        }
        edt1 = popupView.findViewById(R.id.mNumber1Text);
        edt2 = popupView.findViewById(R.id.mNumber2Text);
        edt3 = popupView.findViewById(R.id.mNumber3Text);
        popupView.findViewById(R.id.savebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emphone1 = edt1.getText().toString();
                emphone2 = edt2.getText().toString();
                emmail = edt3.getText().toString();

            }
        });
        // 设置背景图片， 必须设置，不然动画没作用
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(200);

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            lighton();
        }

        // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
        popupWindow.showAtLocation(MainActivity.this.popupView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupView.startAnimation(animation);
    }

    /**
     * 设置手机屏幕亮度变暗
     */
    private void lightoff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    /**
     * 设置手机屏幕亮度显示正常
     */
    private void lighton() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }

    private void startRecord(){
        if(mr == null){
            filepath = Environment.getExternalStorageDirectory().getPath() + "/" + "soundss";
            File dir = new File(filepath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            name = Long.toString(System.currentTimeMillis());
            File soundFile = new File(dir, name + ".amr");
            if(!soundFile.exists()){
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
            mr.setOutputFile(soundFile.getAbsolutePath());
            try {
                mr.prepare();
                mr.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //停止录制，资源释放
    private void stopRecord(){
        if(mr != null){
            try {
                mr.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mr = null;
                mr = new MediaRecorder();
            }
            mr.release();
            mr = null;
        }
    }

    /**
     * 获取具体位置的经纬度
     */
    private void getLocation() {
        // 获取位置管理服务
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        /**这段代码不需要深究，是locationManager.getLastKnownLocation(provider)自动生成的，不加会出错**/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        updateLocation(location);
    }

    /**
     * 获取到当前位置的经纬度
     * @param location
     */
    private void updateLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("经纬度","纬度：" + latitude + "\n经度" + longitude);
        } else {
            Log.e("经纬度","无法获取到位置信息");
        }
    }

    //通过JavaMail发送文件
    private void sendCSVFilByJavaMail() {
        SAXReader reader = new SAXReader();
        Document document = null;
        MailSendInfo info = new MailSendInfo();
        info.setMailServerHost("smtp.sina.com");
        info.setMailServerPost("25");
        info.setValidate(true);
        info.setUserName("acuteboy");
        info.setPassWord("654321lop");//邮箱密码
        info.setFromAddress("15234281406@sina.cn");
        //以下三个内容是需要修改的
        info.setToAddress(emmail);
        info.setSubject("我遇到了危险");
        info.setContent("我是" +
                "请帮助我");
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + "zipsoundss" + "/" + "紧急录音.zip";
        ArrayList<String> Path = new ArrayList<String>(Arrays.asList(path));
        MultiMailSend senMail = new MultiMailSend(path);//这个类用来发送邮件
        Log.e("邮件","准备");
        senMail.sendAttachment(info, Path);
    }

    private void bindBleSevice() {
        Intent serviceIntent = new Intent(MainActivity.this, BleService.class);
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
        Log.e(TAG, "连接");
    }

    //获取BluetoothAdapter
    @SuppressLint("NewApi")
    private void getBleAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) this
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bleAddress = data.getStringExtra("data");
        Log.e(TAG, bleAddress);
        if(bleAddress.equals("w"))
        {
            Log.e(TAG, bleAddress);
            return;
        }
        else bleAddress = bleAddress.substring(0, bleAddress.length());
        Log.e(TAG, bleAddress);
        bindBleSevice();
        registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_CHAR_READED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
        intentFilter.addAction(BleService.ACTION_GATT_RSSI);
        return intentFilter;
    }

}
