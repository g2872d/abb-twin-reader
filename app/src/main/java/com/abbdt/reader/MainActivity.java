package com.abbdt.reader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * 唯一的一个界面：一个铺满全屏的 WebView，加载打包进 assets 的单文件讲解器。
 *
 * 设计原则（故意的）：
 *  - 零权限、零网络：assets/index.html 是自包含单文件，所有样式/脚本/数据都在里面；
 *  - 不介入 HTML 的交互逻辑，全部交给前端处理（返回键除外）；
 *  - 主题色写死为讲解器的深色底，避免启动瞬间白屏闪一下。
 */
public class MainActivity extends Activity {

    private static final String PAGE = "file:///android_asset/index.html";
    private static final int BG = 0xFF0D1014;
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyWindowStyle();

        web = new WebView(this);
        web.setBackgroundColor(BG);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);            // 讲解器全部交互靠 JS
        s.setDomStorageEnabled(true);            // localStorage：记住上次读到哪个文件/字号
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);              // 从 file:// 读 assets
        s.setAllowContentAccess(false);
        s.setSupportZoom(false);                 // 页面缩放交给内置字号按钮，避免手势冲突
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);                      // 不跟随系统字号，否则代码会重排
        s.setLoadWithOverviewMode(false);
        s.setUseWideViewPort(true);              // 让 viewport meta 生效，等于手机浏览器
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 21) {
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 任何外链都不跳浏览器（本页面正常不会有外链）
                return url != null && !url.startsWith("file:///android_asset/");
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);

        setContentView(web);
        web.loadUrl(PAGE);
    }

    /** 深色状态栏 / 导航栏，与 HTML 的 --bg 保持一致，避免上下露白边 */
    private void applyWindowStyle() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {
            w.setStatusBarColor(BG);
            w.setNavigationBarColor(BG);
        }
        View decor = w.getDecorView();
        decor.setSystemUiVisibility(0); // 深色背景 → 用浅色系统图标
    }

    /** 返回键：页面内能后退就后退，否则交回系统（退出） */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web != null && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.loadUrl("about:blank");
            web.removeAllViews();
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }
}
