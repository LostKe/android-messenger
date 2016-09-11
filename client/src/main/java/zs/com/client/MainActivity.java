package zs.com.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Messenger mMessenger;

    Button bt1;
    Button bt2;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mMessenger = new Messenger(service);
                //设置死亡代理
                service.linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bindRemoteService();


    }

    private void bindRemoteService(){
        Intent intent = new Intent();
        intent.setAction("zs.com.service.remoteService");
        bindService(intent, conn, BIND_AUTO_CREATE);
    }


    /**
     * 死亡代理
     */
    private IBinder.DeathRecipient mDeathRecipient=new IBinder.DeathRecipient() {

        //接收到远程链接断开
        @Override
        public void binderDied() {
                if(mMessenger==null){
                    return;
                }else{
                    //释放链接
                    mMessenger.getBinder().unlinkToDeath(mDeathRecipient,0);
                    mMessenger=null;
                    //重新绑定
                    bindRemoteService();
                }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void onClick(View v) {
        Message msg = Message.obtain();
        //设置接收服务端回复的Messenger 通过 replyTo参数传递给服务端
        Messenger replyMessenger=new Messenger(new ReplyMessagHandler());
        msg.replyTo=replyMessenger;
        switch (v.getId()) {
            case R.id.bt1:

                try {
                    msg.what = 1;
                    msg.getData().putString("msg1", "client send msg 1");
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt2:
                try {
                    msg.what = 2;
                    msg.getData().putString("msg2", "client send msg 2");
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 接收 服务端service 回复的消息
     */
   class ReplyMessagHandler extends Handler{
       @Override
       public void handleMessage(Message msg) {
           String replyMsg=msg.getData().getString("reply");
           Toast.makeText(getApplicationContext(),replyMsg,Toast.LENGTH_SHORT).show();
           super.handleMessage(msg);
       }
   }
}
