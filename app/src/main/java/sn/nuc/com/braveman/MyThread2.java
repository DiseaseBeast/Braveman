package sn.nuc.com.braveman;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Lenovo on 2019/6/27.
 */

public class MyThread2 implements Runnable {
    private final String DEBUG_TAG   = "registerActivity";
    private String name;
    private String password;
    private String phonenum;
    private String mailto;
    private String lat;
    private String lon;
    private Boolean stat = false;

    public void setName(String name){
        this.name = name;
    }

    public String Getname(){
        return name;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String GetPassword(){
        return password;
    }

    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    public String GetPhonenum(){
        return phonenum;
    }

    public void setMailto(String mailto){
        this.mailto = mailto;
    }

    public String GetMailto(){
        return mailto;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public String GetLat(){
        return lat;
    }

    public void setLon(String lon){
        this.lon = lon;
    }

    public String GetLon(){
        return lon;
    }

    public Boolean GetStat(){
        return stat;
    }

    public void run(){
        try {
            BufferedReader br = null;
            Socket socket = null;
            //创建Socket
            socket = new Socket("192.168.137.1", 54321);
            //socket = new Socket("10.14.114.127",54321); //IP：10.14.114.127，端口54321
            //向服务器发送消息
            Log.e(DEBUG_TAG, "发送");
            //OutputStream os = socket.getOutputStream();
            //PrintWriter pw = new PrintWriter(os);
            //InputStream is = socket.getInputStream();
            //br = new BufferedReader(new InputStreamReader(is));
            //pw.write(msg);
            //pw.flush();
            //socket.shutdownOutput();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println("regi");
            out.println(name);
            out.println(password);
            out.println(phonenum);
            out.println(mailto);
            out.println(lat);
            out.println(lon);

            //接收来自服务器的消息
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String st = br.readLine();
            Log.e(DEBUG_TAG, st);
            if(st.equals("yes")) stat = true;
            //关闭流
            //pw.close();
            //is.close();
            //os.close();
            out.close();
            br.close();
            //关闭Socket
            socket.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(DEBUG_TAG, e.toString());
        }

    }
}
