package com.google.android.exoplayer.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.exoplayer.SmoothStreamingTestMediaDrmCallback;
import com.google.android.exoplayer.WidevineTestMediaDrmCallback;
import com.google.android.exoplayer.util.Util;

/**
 * @项目名: MyExoPlayer
 * @包名: com.google.android.exoplayer.exoplayer
 * @类名: PlayerUtils
 * @作者: yp
 * @创建时间: 2017/01/08 12:39
 * @描述: TODO:
 */

public class PlayerUtils {

    public static int inferContentType(Uri uri) {
        String u = uri.toString();
        String lastPathSegment = u.substring(u.lastIndexOf("."));
        return Util.inferContentType(lastPathSegment);
    }


    public  static DemoPlayer.RendererBuilder getRendererBuilder(Context context,int contentType, Uri contentUri ) {
        String userAgent = Util.getUserAgent(context, "MyExoPlayer");
        switch (contentType) {
            case Util.TYPE_SS:
                return new SmoothStreamingRendererBuilder(context, userAgent, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case Util.TYPE_DASH:
                return new DashRendererBuilder(context, userAgent, contentUri.toString(),
                        new WidevineTestMediaDrmCallback(null, null));
            //new WidevineTestMediaDrmCallback(contentId, provider));
            case Util.TYPE_HLS:
                return new HlsRendererBuilder(context, userAgent, contentUri.toString());
            case Util.TYPE_OTHER:
                return new ExtractorRendererBuilder(context, userAgent, contentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }


    public static void scaleLayout(Context context,View view, int width, int height) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        if (width == 0) {
            width=outMetrics.widthPixels;
        }
        if (height == 0) {
            height = outMetrics.heightPixels;
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(width, height);
        } else {
            params.height = height;
            params.width = width;
        }

        view.setLayoutParams(params);
    }
}
