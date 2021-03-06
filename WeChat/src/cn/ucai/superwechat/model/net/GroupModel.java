package cn.ucai.superwechat.model.net;

import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.utils.OkHttpUtils;

/**
 * Created by xheng on 2017/4/10.
 */

public class GroupModel implements IGroupModel {
    @Override
    public void newGroup(Context context, String hxid, String gName, String desc, String owner,
                         boolean isPublic, boolean isInvites, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,hxid)
                .addParam(I.Group.NAME,gName)
                .addParam(I.Group.DESCRIPTION,desc)
                .addParam(I.Group.OWNER,owner)
                .addParam(I.Group.IS_PUBLIC, String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES, String.valueOf(isInvites))
                .addFile2(file)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void addMembers(Context context, String members, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.USER_NAME,members)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void findGroupInfoByHxid(Context context, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_HXID)
                .addParam(I.Group.HX_ID, hxid)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void delMember(Context context, String members, String groupId, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP_MEMBER)
                .addParam(I.Member.USER_NAME,members)
                .addParam(I.Member.GROUP_ID,groupId)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void updateGroupName(Context context, String newName, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_GROUP_NAME)
                .addParam(I.Group.NAME,newName)
                .addParam(I.Group.HX_ID,hxid)
                .targetClass(String.class)
                .execute(listener);
    }
}
