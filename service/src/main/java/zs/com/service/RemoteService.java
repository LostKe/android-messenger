package zs.com.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * 典型的服务端代码
 *
 * Messenger 远程服务
 * Created by zhangshuqing on 16/9/11.
 */
public class RemoteService  extends Service{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Messenger mMessenger=new Messenger(new MessengerHandler());
        return mMessenger.getBinder();
    }

    class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.d(">>>>>>>>>>>>>>>>>>>>remoteService","handler msg");
           Bundle bundle=msg.getData();
            String receviceMsg="";
            switch (msg.what){

                //TODO 服务端收到消息后的工作
                case 1:
                    receviceMsg=bundle.getString("msg1");
                    break;
                case 2:
                    receviceMsg=bundle.getString("msg2");
                    break;
                default:
                    super.handleMessage(msg);

            }
            try {
                //拿到回复客户端消息的 Messenger
                Messenger mReplyTo= msg.replyTo;
                Message replyMessage=Message.obtain();
                replyMessage.getData().putString("reply","i'am service i have recevice you msg!!!");
                mReplyTo.send(replyMessage);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(">>>>>>>>>>>>>>>>>>>>remoteService",receviceMsg);
            //在service中发通知
            NotificationManager mNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("New Message")
                    
                    .setContentText(receviceMsg)
                    .setSmallIcon(R.mipmap.ic_launcher);
            mNotificationManager.notify(0,mNotifyBuilder.build());


        }
    }


}
