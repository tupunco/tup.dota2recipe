/**
 * 
 */
package tup.dota2recipe;

import tup.dota2recipe.util.ExtendedImageDownloader;
import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class DefaultApplication extends Application {

    /**
     * singleton
     */
    private static DefaultApplication globalContext = null;

    @Override
    public void onCreate() {
        super.onCreate();

        globalContext = this;

        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .memoryCacheSize(2 * 1024 * 1024)
                        .denyCacheImageMultipleSizesInMemory()
                        // .discCacheFileNameGenerator(new
                        // Md5FileNameGenerator())
                        .imageDownloader(
                                new ExtendedImageDownloader(
                                        getApplicationContext()))
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .enableLogging()
                        .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * Global Context
     * 
     * @return
     */
    public static DefaultApplication getInstance() {
        return globalContext;
    }
}
