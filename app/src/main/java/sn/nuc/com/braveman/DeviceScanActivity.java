package sn.nuc.com.braveman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class DeviceScanActivity extends Activity{
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";
    public static final String DISCONNECT_DEVICE = "find.device.disconnect";
    public static final String CANCEL_DEVICE_ALARM = "find.device.cancel.alarm";
    public static final String DEVICE_BATTERY = "device.battery.level";

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private boolean bind;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private Handler mhandler = new Handler();
    private BleDeviceListAdapter mLeDeviceListAdapter;
    private ListView listView;
    private final static int REQUEST_ENABLE_BT = 1;
    private String TAG = "BleDeviceListAdapter";
    public static String bleAddress;
    List<BluetoothGattService> gattServices = new ArrayList<BluetoothGattService>();

    private static final long SCAN_PERIOD = 15000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  //设置窗口显示模式为窗口方式
        setContentView(R.layout.activity_device_scan);

        // 设定默认返回值为取消
        setResult(Activity.RESULT_CANCELED);
        listView = (ListView) findViewById(R.id.paired_devices);

        getBleAdapter();
        //开启蓝牙
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.enable();
            Log.e(TAG, "开启蓝牙");
        }

        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getScanResualt();
                scanLeDevice(true);
                v.setVisibility(View.GONE);
                Log.e(TAG,"结束！");
            }
        });
        bind = false;
        setListItemListener();
    }



    //获取BluetoothAdapter
    @SuppressLint("NewApi")
    private void getBleAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) this
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @SuppressLint("NewApi")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mhandler.postDelayed(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            if(mLeDeviceListAdapter != null){
                mLeDeviceListAdapter.clear();
            }
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mLeDeviceListAdapter = new BleDeviceListAdapter(this);
            listView.setAdapter(mLeDeviceListAdapter);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @SuppressLint("NewApi")
    private void getScanResualt() {
        mLeScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, int rssi,
                                         byte[] scanRecord) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                                invalidateOptionsMenu();
                            }
                        });
                    }
                };
    }

    private void setListItemListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                bind = true;
                BluetoothDevice device = mLeDeviceListAdapter
                        .getDevice(position);
                bleAddress = device.getAddress();
                Intent intent = new Intent();
                intent.putExtra("data", bleAddress);
                setResult(3, intent);
                finish();
            }
        });
    }

    public void OnCancel(View v){
        bleAddress = "w";
        Intent intent = new Intent();
        intent.putExtra("data", bleAddress);
        setResult(3, intent);
        finish();
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLeDeviceListAdapter.clear();
        mhandler.removeCallbacksAndMessages(null);

    }
}
