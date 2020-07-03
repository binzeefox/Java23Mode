package com.binzeefox.java23mode.image_loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小民的图片加载类 v0.1
 *
 * 没有使用设计模式
 * @author binze
 * 2020/7/3 14:24
 */
// 不知道咋说。。。确确实实没有设计模式。我也欣慰的发现自己绝对不会写出这种代码来。
// 不说可拓展性抽象性啥的。。。貌似这还有内存泄漏。
public class ImageLoader {
    private static final String TAG = "ImageLoader";

    private LruCache<String, Bitmap> mImageCache;   //缓存
    private ExecutorService mService    //线程池，线程数量为CPU数量
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Handler mUIHandler = new Handler(Looper.getMainLooper());   //UI线程

    public ImageLoader(){
        initImageCache();
    }

    /**
     * 初始化缓存
     * @author binze
     * 2020/7/3 14:28
     */
    private void initImageCache() {
        final double maxMemory = Runtime.getRuntime().maxMemory() >> 10;    //最大可用内存
        final double cacheSize = maxMemory / 4; //四分之一内存为缓存
        mImageCache = new LruCache<String, Bitmap>((int) cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() >> 10;
            }
        };
    }

    /**
     * 将图片加载进imageView
     * @author binze
     * 2020/7/3 14:33
     */
    public void displayImage(final String url, final ImageView imageView){
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
