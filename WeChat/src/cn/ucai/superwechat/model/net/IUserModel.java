package cn.ucai.superwechat.model.net;

import android.content.Context;

/**
 * Created by xheng on 2017/3/29.
 */

public interface IUserModel {
    void login(Context context, String userName, String password,
               OnCompleteListener<String> listener);

    void register(Context context, String userName, String nick, String password,
                  OnCompleteListener<String> listener);

    void unRegister(Context context,String userName,
                    OnCompleteListener<String> listener);

    void loadUserInfo(Context context, String userName, OnCompleteListener<String> listener);

    void upDateNick(Context context, String userName, String nick, OnCompleteListener<String> listener);
}
