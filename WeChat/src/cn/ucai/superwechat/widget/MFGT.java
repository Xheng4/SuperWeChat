package cn.ucai.superwechat.widget;

import android.app.Activity;
import android.content.Intent;

import cn.ucai.superwechat.R;

/**
 * Created by xheng on 2017/3/29.
 */

public class MFGT {
    public static void startActivity(Activity activity, Class cla) {
        activity.startActivity(new Intent(activity,cla));
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
    public static void finish(Activity activity) {
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        activity.finish();
    }
}
