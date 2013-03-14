package tup.dota2recipe.util;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Abstract AsyncTaskLoader
 * 
 * FROM:/android-sdk/samples/android-17/ApiDemos/src/com/example/android
 * /apis/app/LoaderCustom.java
 * 
 * @param <D>
 */
public abstract class AbstractAsyncTaskLoader<D> extends AsyncTaskLoader<List<D>> {
    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    protected List<D> cDataList = null;

    public AbstractAsyncTaskLoader(Context context) {
        super(context);
    }

    /**
     * 
     */
    @Override
    public abstract List<D> loadInBackground();

    @Override
    public void deliverResult(List<D> cList) {
        if (isReset()) {
            if (cList != null) {
                onReleaseResources(cList);
            }
        }
        List<D> oldList = cList;
        cDataList = cList;

        if (isStarted()) {
            super.deliverResult(cList);
        }

        if (oldList != null) {
            onReleaseResources(oldList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (cDataList != null) {
            deliverResult(cDataList);
        }

        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || cDataList == null || configChange) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<D> cList) {
        super.onCanceled(cList);

        onReleaseResources(cList);
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();
        if (cDataList != null) {
            onReleaseResources(cDataList);
            cDataList = null;
        }
    }

    /**
     * 
     * @param cList
     */
    protected void onReleaseResources(List<D> cList) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
