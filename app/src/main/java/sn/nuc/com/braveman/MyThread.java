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

public class MyThread implements Runnable {
    private final String DEBUG_TAG   = "loginActivity";
    private String msg;
    private Boolean stat = true;
    private String phonenum;
    private String mailto;

    public void setMsg(String msg){
        this.msg = msg;
    }

    public String GetMsg(){
        return msg;
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

    public void SetStat(){
        this.stat = false;
    }

    public void run(){
        //while (stat) {
        try {
            BufferedReader br = null;
            Socket socket = null;
            //创建Socket
            socket = new Socket("192.168.137.1", 54321);
            //socket = new Socket("10.14.114.127",54321); //IP：10.14.114.127，端口54321
            //向服务器发送消息
            Log.e("ser", "发送");
            //OutputStream os = socket.getOutputStream();
            //PrintWriter pw = new PrintWriter(os);
            //InputStream is = socket.getInputStream();
            //br = new BufferedReader(new InputStreamReader(is));
            //pw.write(msg);
            //pw.flush();
            //socket.shutdownOutput();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println("login");
            out.println(msg);

            //接收来自服务器的消息
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msg = br.readLine();
            phonenum = br.readLine();
            mailto = br.readLine();
            Log.e(DEBUG_TAG, msg);
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
        //try {
        //    Thread.sleep(10*1000);//每次间隔10秒钟。
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }
}

