package com.hyphenate.easeui.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    /**
     * WeChat
     * get EaseUser according username
     * @param username
     * @return
     */
    public static User getWeChatUserInfo(String username){
        if(userProvider != null)
            return userProvider.getWeChatUser(username);

        return null;
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setGroupAvatar(Context context, String username, ImageView imageView){
    	User user = getWeChatUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    /**
     * WeChat
     * set user avatar
     * @param username
     */
    public static void setWeChatUserAvatar(Context context, String username, ImageView imageView){
    	User user = getWeChatUserInfo(username);
       setWeChatUserAvatar(context,user,imageView);
    }

    /**
     * WeChat
     * set user avatar
     *
     * @param user
     */
    public static void setWeChatUserAvatar(Context context, User user, ImageView imageView) {
//        Glide.with(context).load(user.getAvatar()).into(imageView);
        if (user != null && user.getAvatar() != null) {
            setWeChatAvatar(context,user.getAvatar(), imageView);
        } else {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    public static void setWeChatAvatar(Context context, String avatar, ImageView imageView) {
        try {
            int avatarResId = Integer.parseInt(avatar);
            Glide.with(context).load(avatarResId).into(imageView);
        } catch (Exception e) {
            //use default avatar
            Glide.with(context).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    /**
     * WeChat
     * set user's nickname
     */
    public static void setWeChatUserNick(String username,TextView textView){
        if(textView != null){
        	User user = getWeChatUserInfo(username);
        	setWeChatUserNick(user,textView);
        }
    }
 /**
     * WeChat
     * set user's nickname 0
     */
    public static void setWeChatUserNick(User user,TextView textView){
        if(textView != null){
        	if(user != null && user.getMUserNick() != null){
        		textView.setText(user.getMUserNick());
        	}else{
        		textView.setText(user.getMUserName());
        	}
        }
    }

}
