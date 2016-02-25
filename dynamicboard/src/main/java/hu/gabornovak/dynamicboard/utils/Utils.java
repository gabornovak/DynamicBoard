package hu.gabornovak.dynamicboard.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by gnovak on 2/7/2016.
 */
public class Utils {
    private static final boolean LOG_ENABLED = true;

    public static void log(String msg) {
        if (LOG_ENABLED) {
            Log.d("DynamicBoard", msg);
        }
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
