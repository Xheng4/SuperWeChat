package cn.ucai.superwechat.model.net;

import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by xheng on 2017/3/29.
 */

public class UserModel implements IUserModel {
    @Override
    public void login(Context context, String userName, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.PASSWORD, password)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void register(Context context, String userName, String nick, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME, userName)
                .addParam(I.User.NICK, nick)
                .addParam(I.User.PASSWORD, password)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void unRegister(Context context, String userName, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void loadUserInfo(Context context, String userName, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void upDateNick(Context context, String userName, String nick, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.NICK,nick)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void updateAvatar(Context context, String userName, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID,userName)
                .addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }
}
