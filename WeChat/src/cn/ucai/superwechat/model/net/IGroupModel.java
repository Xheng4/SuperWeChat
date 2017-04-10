package cn.ucai.superwechat.model.net;

import android.content.Context;

import java.io.File;

/**
 * Created by xheng on 2017/4/10.
 */

public interface IGroupModel {
    void newGroup(Context context, String hixd, String gName, String desc, String owner,
                  boolean isPublic, boolean isInvites, File file, OnCompleteListener<String> listener);
}
