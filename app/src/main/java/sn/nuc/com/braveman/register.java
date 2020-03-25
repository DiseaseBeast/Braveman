package sn.nuc.com.braveman;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class register extends Activity {
    private EditText edittext1, edittext2, edittext3, edittext4, edittext5;
    private Button button;
    private RadioGroup rb_1;
    String lat = "0";
    String lon = "0";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        edittext1 = (EditText) findViewById(R.id.editview1);
        edittext2 = (EditText) findViewById(R.id.editview2);
        edittext3 = (EditText) findViewById(R.id.editview3);
        edittext4 = (EditText) findViewById(R.id.editview4);
        button = (Button) findViewById(R.id.regis);
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String namestring = edittext1.getText().toString();
                String passstring = edittext2.getText().toString();
                String phonenum = edittext3.getText().toString();
                String mailto = edittext4.getText().toString();

                if (namestring.equals("") || passstring.equals("") || phonenum.equals("")||mailto.equals("")) {
                    //弹出消息框
                    new AlertDialog.Builder(register.this).setTitle("错误")
                            .setMessage("不能有空").setPositiveButton("确定", null)
                            .show();
                } else {
                    getLocation();
                    regi(namestring, passstring, phonenum, mailto, lat, lon);
                }
            }

        });
    }
    public void regi(String name, String password, String phonenum, String mailto, String lat, String lon)
    {
        try {
            new String(name.getBytes("iso-8859-1"),"utf-8");
            //    URLEncoder.encode(message, "utf-8");
        }catch (UnsupportedEncodingException u){
            u.printStackTrace();
        }
        Log.e("regi", name);
        MyThread2 mythread2 = new MyThread2();
        mythread2.setName(name);
        mythread2.setPassword(password);
        mythread2.setPhonenum(phonenum);
        mythread2.setMailto(mailto);
        mythread2.setLat(lat);
        mythread2.setLon(lon);
        Thread thread = new Thread(mythread2);
        thread.start();
        try {
            thread.join();
            //Log.e("serv", mythread.GetMsg());
            if (mythread2.GetStat()) {
                Toast.makeText(register.this,"注册成功！", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(register.this,MainActivity.class);
                intent.putExtra("phonenum", phonenum);
                intent.putExtra("mailto", mailto);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(register.this).setTitle("错误")
                        .setMessage("注册失败！").setPositiveButton("确定", null)
                        .show();
            }
        } catch (InterruptedException i) {
            Log.e("ser", "错误!");
            new AlertDialog.Builder(register.this).setTitle("错误")
                    .setMessage("网络错误").setPositiveButton("确定", null)
                    .show();
        }
    }

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
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
            Log.e("经纬度","纬度：" + lat + "\n经度" + lon);
        } else {
            Log.e("经纬度","无法获取到位置信息");
        }
    }
}