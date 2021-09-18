package manager.dowload;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.Logger;

import java.io.File;

/**
 * Created by hossein on 8/30/16.
 * Read More : http://101apps.co.za/articles/using-the-downloadmanager-to-manage-your-downloads.html
 */
public class NotificationDLManager {

    private static NotificationDLManager instance;

    DownloadManager downloadManager;
    private long myDownloadRefrence;
    private BroadcastReceiver receiverDownloadComplete;
    private BroadcastReceiver receiverNotificationClicked;
    IntentFilter filter;

    private NotificationDLManager(){
        downloadManager = (DownloadManager) PoinilaApplication.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
//        filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
//        receiverNotificationClicked = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String extraId = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
//
//                long[] refrences = intent.getLongArrayExtra(extraId);
//
//                for(long refrence : refrences){
//                    if(refrence == myDownloadRefrence){
//
//                    }
//                }
//
//                PoinilaApplication.getAppContext().registerReceiver(receiverNotificationClicked, filter);
//            }
//        };
    }

    public static NotificationDLManager getInstance(){
        if(instance == null)
            instance = new NotificationDLManager();
        return instance;
    }

    public void download(String url, String name, String description){
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request
                .setTitle(name)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription(description)
                .setVisibleInDownloadsUi(true)
//                .setDestinationInExternalFilesDir() this method is for save in package name directory
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,  new File(url).getName());

        try{
            downloadManager.enqueue(request);
        }catch (Exception e){
            Logger.toastError(R.string.download_error);
        }


    }
}
