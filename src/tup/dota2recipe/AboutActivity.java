package tup.dota2recipe;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class AboutActivity extends SwipeBackActivity {

    WebView webv_about_desc = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        webv_about_desc = (WebView) this.findViewById(R.id.webv_about_desc);
        webv_about_desc.getSettings().setJavaScriptEnabled(true);
        webv_about_desc.addJavascriptInterface(new JsCallbackObj(this), "aboutCallback");
        webv_about_desc.loadUrl("file:///android_asset/about/about.html");
    }

    /**
     * 
     */
    public class JsCallbackObj
    {
        Context mContext;

        public JsCallbackObj(Context context)
        {
            this.mContext = context;
        }

        /**
         * get soft version
         * 
         * @param key
         * @return
         */
        @JavascriptInterface
        @SuppressLint("DefaultLocale")
        public String getVersion()
        {
            String versionName = "-.-";
            int versionCode = 0;
            PackageManager pm = getPackageManager();
            try {
                String pkgname = AboutActivity.this.getPackageName();
                PackageInfo info = pm.getPackageInfo(pkgname, 0);
                versionName = info.versionName;
                versionCode = info.versionCode;
            } catch (NameNotFoundException e) {
            }
            return String.format("%s (%d)", versionName, versionCode);
        }

        /**
         * get data version
         * 
         * @param key
         * @return
         */
        @JavascriptInterface
        public String getDataVersion()
        {
            return this.mContext
                    .getResources()
                    .getString(R.string.assets_about_dataVersion);
        }

        /**
         * MailTo
         * 
         * @param to
         * @param subject
         */
        @JavascriptInterface
        public void mailTo(String to, String subject) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
            if (!TextUtils.isEmpty(subject))
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);

            intent = Intent.createChooser(intent, null);
            if (intent != null) {
                this.mContext.startActivity(intent);
            }
        }

        /**
         * Show Toast
         * 
         * @param msg
         */
        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
