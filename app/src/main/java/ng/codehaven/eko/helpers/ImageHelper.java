package ng.codehaven.eko.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ng.codehaven.eko.utils.Logger;

/**
 * Created by Thompson on 01/02/2015.
 */
public class ImageHelper {

    public static void storeImage(Context ctx, Bitmap image, String fileName) {
        File pictureFile = getOutputMediaFile(ctx, fileName);
        if (pictureFile == null) {
            Logger.m("Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Logger.m("File not found: " + e.getMessage());
        } catch (IOException e) {
            Logger.m("Error accessing file: " + e.getMessage());
        }
    }

    /**
     * Create a File for saving an image or video
     *
     * @param ctx      Context
     * @param fileName File Name
     */
    private static File getOutputMediaFile(Context ctx, String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + ctx.getPackageName()
                + "/Files/.temp/images");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = fileName + "_MI_" + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static boolean fileExists(Context ctx, String id) {
        File file = getFile(ctx, id);

        return file.exists();

    }

    public static Uri getImageURI(Context ctx, String id) {
        if (fileExists(ctx, id)) {
            return Uri.fromFile(getFile(ctx, id));
        }
        return null;
    }

    private static File getFile(Context ctx, String id) {
        return new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + ctx.getPackageName()
                + "/Files/.temp/images/"
                + id
                + "_MI_"
                + ".jpg");
    }

    public static Bitmap getImage(Uri uri) {
        if (uri != null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(uri.getEncodedPath(), options);
        }
        return null;
    }
}
