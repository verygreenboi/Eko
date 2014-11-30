package ng.codehaven.eko;

import android.graphics.Bitmap;
import android.hardware.Camera;

import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import ng.codehaven.eko.utils.ImageCacheManager;
import ng.codehaven.eko.utils.RequestManager;

/**
 * Created by mrsmith on 11/9/14.
 * Base Application
 */
public class Application extends SugarApp {
    private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided


    @Override
    public void onCreate() {
        Parse.initialize(this, getString(R.string.app_id), getString(R.string.app_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();


        init();

        super.onCreate();
    }

    /**
     * Intialize the request manager and the image cache
     */
    private void init() {
        RequestManager.init(this);
        createImageCache();
    }

    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
     */
    private void createImageCache(){
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGECACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public static void UpdateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(Constants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
