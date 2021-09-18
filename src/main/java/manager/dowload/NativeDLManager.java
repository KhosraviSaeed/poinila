//package manager.dowload;
//
//import com.shaya.dlm.core.DownloadManagerPro;
//import com.shaya.dlm.report.listener.DownloadManagerListener;
//import com.shaya.poinila.android.presentation.PoinilaApplication;
//import com.shaya.poinila.android.presentation.R;
//import static com.shaya.poinila.android.util.ResourceUtils.*;
//
///**
// * Created by hossein on 8/30/16.
// */
//public class NativeDLManager implements DownloadManagerListener {
//
//    // TODO: For read more go to https://github.com/majidgolshadi/Android-Download-Manager-Pro
//    DownloadManagerPro dLManager;
//
//    private NativeDLManager(){
//        dLManager = new DownloadManagerPro(PoinilaApplication.getAppContext());
//        dLManager.init(getString(R.string.app_name), 10, this);
//    }
//
//    @Override
//    public void OnDownloadStarted(long taskId) {
//
//    }
//
//    @Override
//    public void OnDownloadPaused(long taskId) {
//
//    }
//
//    @Override
//    public void onDownloadProcess(long taskId, double percent, long downloadedLength) {
//
//    }
//
//    @Override
//    public void OnDownloadFinished(long taskId) {
//
//    }
//
//    @Override
//    public void OnDownloadRebuildStart(long taskId) {
//
//    }
//
//    @Override
//    public void OnDownloadRebuildFinished(long taskId) {
//
//    }
//
//    @Override
//    public void OnDownloadCompleted(long taskId) {
//
//    }
//
//    @Override
//    public void connectionLost(long taskId) {
//
//    }
//}
