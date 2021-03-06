package cn.ucai.superwechat.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.domain.InviteMessage;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.AddFriendCheckActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.GroupsActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.NewFriendsMsgActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SearchUserProfileActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;
import cn.ucai.superwechat.ui.WelcomeActivity;

/**
 * Created by xheng on 2017/3/29.
 */

public class MFGT {
    public static void startActivity(Activity activity, Class cla) {
        activity.startActivity(new Intent(activity, cla));
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void startActivityZ(Activity activity, Class cla) {
        activity.startActivity(new Intent(activity, cla));
        activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void finish(Activity activity) {
        activity.overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
        activity.finish();
    }

    public static void finishZ(Activity activity) {
        activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        activity.finish();
    }

    public static void gotoMain(Activity activity) {
        startActivity(activity, MainActivity.class);
    }

    public static void gotoWelcome(Activity activity) {
        startActivity(activity, WelcomeActivity.class);
    }

    public static void gotoLogin(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }

    public static void logoutToLogin(Activity activity) {
        startActivity(activity, new Intent(activity, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void gotoRegister(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }

    public static void gotoUserProfileActivity(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }

    public static void gotoSettingsActivity(Activity activity) {
        startActivity(activity, SettingsActivity.class);
    }

    public static void gotoAddContactActivity(Activity activity) {
        startActivityZ(activity, AddContactActivity.class);
    }

    public static void gotoSearchUserProfile(Activity activity, User user) {
        startActivity(activity, new Intent(activity, SearchUserProfileActivity.class).
                putExtra("user", user));
    }

    public static void gotoSearchUserProfile(Context activity, InviteMessage msg) {
        startActivity((Activity)activity, new Intent(activity, SearchUserProfileActivity.class).
                putExtra("msg", msg));
    }

    public static void gotoAddFriendCheck(Activity activity, String name) {
        startActivity(activity, new Intent(activity, AddFriendCheckActivity.class)
                .putExtra(I.User.USER_NAME, name));
    }

    public static void gotoNewFriendMsgActivity(Activity activity) {
        startActivity(activity, NewFriendsMsgActivity.class);
    }

    public static void gotoChat(Activity activity, String userName) {
        startActivity(activity, new Intent(activity, ChatActivity.class)
                .putExtra("userId", userName));
    }

    public static void gotoMain(Activity activity, boolean b) {
        startActivity(activity,new Intent(activity,ChatActivity.class)
                .putExtra(I.IS_FROM_CHAT,b));
    }

    public static void gotoSearchUserProfile(Activity activity, String username) {
        startActivity((Activity)activity,new Intent(activity,SearchUserProfileActivity.class)
                .putExtra(I.User.USER_NAME,username));
    }

    public static void gotoGroups(Activity activity) {
        startActivity(activity, GroupsActivity.class);
    }
}
