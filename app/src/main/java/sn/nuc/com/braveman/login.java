package sn.nuc.com.braveman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class login extends Activity {
    private TextView textview;
    private Button button1;
    private EditText nameText,passText;
    private String phonenum,mailto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        nameText=(EditText)findViewById(R.id.username);
        passText=(EditText)findViewById(R.id.pasw);

        button1=(Button)findViewById(R.id.submit);

        //启动注册页面
        TextView textview=(TextView)findViewById(R.id.zhuce);
        textview.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent a=new Intent(login.this,register.class);
                startActivity(a);
            }

        });

        //启动主页面
        TextView textview1=(TextView)findViewById(R.id.suibian);
        textview1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(login.this,MainActivity.class);
                phonenum = "";
                mailto = "";
                intent.putExtra("phonenum", phonenum);
                intent.putExtra("mailto", mailto);
                startActivity(intent);
            }

        });
        button1.setOnClickListener(new LoginListener());
    }


    class LoginListener implements View.OnClickListener {
        public void onClick(View v){
            String nameString =nameText.getText().toString();
            String passString=passText.getText().toString();
            if(nameString.equals("")||passString.equals(""))
            {
                //弹出消息框
                new AlertDialog.Builder(login.this).setTitle("错误")
                        .setMessage("帐号或密码不能空").setPositiveButton("确定", null)
                        .show();
            }else{
                isUserinfo(nameString,passString);

            }
        }
    }

    public void isUserinfo(String name, String pass)
    {
        String username = name;
        String password = pass;
        boolean nam = false;
        try {
            new String(username.getBytes("iso-8859-1"),"utf-8");
            //    URLEncoder.encode(message, "utf-8");
        }catch (UnsupportedEncodingException u){
            u.printStackTrace();
        }
        Log.e("serv", username);
        MyThread mythread = new MyThread();
        mythread.setMsg(username);
        Thread thread = new Thread(mythread);
        thread.start();
        try {
            thread.join();
            //Log.e("serv", mythread.GetMsg());
            if (mythread.GetMsg().equals(password)) {
                Intent intent=new Intent(login.this,MainActivity.class);
                phonenum = mythread.GetPhonenum();
                mailto = mythread.GetMailto();
                intent.putExtra("phonenum", phonenum);
                intent.putExtra("mailto", mailto);
                Log.e("login", phonenum);
                Log.e("login", mailto);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(login.this).setTitle("错误")
                        .setMessage("帐号或密码错误").setPositiveButton("确定", null)
                        .show();
            }
        } catch (InterruptedException i) {
            Log.e("ser", "错误!");
            new AlertDialog.Builder(login.this).setTitle("错误")
                    .setMessage("网络错误").setPositiveButton("确定", null)
                    .show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

}