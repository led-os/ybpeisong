package com.ybguajia.ybtest.utils.net;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.ybguajia.ybtest.R;

public class ImgUtils {

    private ImageLoader mImageLoader = null;
    private DisplayImageOptions options = null;
    private static ImgUtils mImageUtils = null;

    public static ImgUtils getInstance(Context context) {
        if (mImageUtils == null) {
            synchronized (ImgUtils.class) {
                if (mImageUtils == null) {
                    mImageUtils = new ImgUtils(context);
                }
            }
        }
        return mImageUtils;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private ImgUtils(Context context) {
        mImageLoader = ImageLoader.getInstance();
        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 5);
        MemoryCacheAware<String, Bitmap> memoryCache;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            memoryCache = new LruMemoryCache(memoryCacheSize);
        } else {
            memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
        }

        options = new DisplayImageOptions.Builder().showStubImage(R.mipmap.http_loading).showImageForEmptyUri(R
                .mipmap.http_loading).showImageOnFail(R.mipmap.http_error).imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true).cacheOnDisc(true).bitmapConfig(Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions
                (options).denyCacheImageMultipleSizesInMemory().discCache(new UnlimitedDiscCache(StorageUtils
                .getCacheDirectory(context))).discCacheFileNameGenerator(new Md5FileNameGenerator()).memoryCache
                (memoryCache).memoryCacheSize(memoryCacheSize).tasksProcessingOrder(QueueProcessingType.LIFO)
                .threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3).build();

        ImageLoader.getInstance().init(config);
    }

    public static int getMemoryCacheSize(Context context) {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
            // limit
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }
        return memoryCacheSize;
    }

    /**
     * 清除硬盘缓存
     */
    public void clearDiskCache() {
        if (mImageLoader != null) {
            mImageLoader.clearDiscCache();
        }
    }

    /**
     * 清除内存缓存
     */
    public void clearMemoryCache() {
        if (mImageLoader != null) {
            mImageLoader.clearMemoryCache();
        }
    }

    /**
     * 获取图片宽度
     */
    public static int getImgWidth(Context context, int imgId) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), imgId);
        return bm.getWidth();
    }
}
