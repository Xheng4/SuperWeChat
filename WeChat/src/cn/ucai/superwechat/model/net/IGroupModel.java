package cn.ucai.superwechat.model.net;

import android.content.Context;

import java.io.File;

/**
 * Created by xheng on 2017/4/10.
 */

public interface IGroupModel {
    void newGroup(Context context, String hixd, String gName, String desc, String owner,
                  boolean isPublic, boolean isInvites, File file, OnCompleteListener<String> listener);

    void addMembers(Context context, String members, String hxid, OnCompleteListener<String> listener);

    void findGroupInfoByHxid(Context context,String hxid,OnCompleteListener<String> listener);

    void delMember(Context context, String members, String groupId, OnCompleteListener<String> listener);

    void updateGroupName(Context context, String newName, String groupId, OnCompleteListener<String> listener);
}
