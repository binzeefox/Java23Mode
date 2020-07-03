package com.binzeefox.java23mode.image_loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小民的图片加载类 v0.2
 *
 * 单一职责原则
 * @author binze
 * 2020/7/3 14:24
 */
// 分离了图片加载和图片缓存两部分职责
public class ImageLoader {
    private static final String TAG = "ImageLoader";

    private ImageCache mImageCache = new ImageCache();   //自定义缓存
    private ExecutorService mService    //线程池，线程数量为CPU数量
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Handler mUIHandler = new Handler(Looper.getMainLooper());   //UI线程


    /**
     * 将图片加载进imageView
     * @author binze
     * 2020/7/3 14:33
     */
    public void displayImage(final String url, final ImageView imageView){
        //若存在缓存则加载缓存
        Bitmap bitmap = mImageCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        //否则下载图片
        imageView.setTag(url);
        mService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url);
                if (bitmap == null) return;
                if (imageView.getTag().equals(url)){
                    updateImageView(imageView, bitmap);
                }
                mImageCache.put(url, bitmap);
            }
        });
    }

    /**
     * 更新视图
     * @author binze 2020/7/3 14:36
     */
    private void updateImageView(final ImageView imageView, final Bitmap bitmap) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 下载图片
     * @author binze 2020/7/3 14:37
     */
    private Bitmap downloadImage(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
