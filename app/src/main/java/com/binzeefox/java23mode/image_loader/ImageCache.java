package com.binzeefox.java23mode.image_loader;

import android.graphics.Bitmap;

import org.jetbrains.annotations.NotNull;

import androidx.collection.LruCache;

/**
 * 图片缓存实现类
 * @author binze
 * 2020/7/3 14:46
 */
public class ImageCache {
    private LruCache<String, Bitmap> mCache;

    public ImageCache(){
        init();
    }

    /**
     * 初始化缓存
     * @author binze 2020/7/3 14:48
     */
    private void init() {
        final double maxMemory = Runtime.getRuntime().maxMemory() >> 10;    //最大可用内存
        final double cacheSize = maxMemory / 4; //四分之一内存为缓存
        mCache = new LruCache<String, Bitmap>((int) cacheSize){
            @Override
            protected int sizeOf(@NotNull String key, @NotNull Bitmap value) {
                return value.getRowBytes() * value.getHeight() >> 10;
            }
        };
    }

    /**
     * 添加缓存
     * @author binze 2020/7/3 14:52
     */
    public void put(String url, Bitmap bitmap){
        mCache.put(url, bitmap);
    }

    /**
     * 加载缓存
     * @author binze 2020/7/3 14:53
     */
    public Bitmap get(String url){
        return mCache.get(url);
    }
}
