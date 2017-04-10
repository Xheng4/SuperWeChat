/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.model.bean.Result;
import cn.ucai.superwechat.model.net.GroupModel;
import cn.ucai.superwechat.model.net.IGroupModel;
import cn.ucai.superwechat.model.net.OnCompleteListener;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.MFGT;

public class NewGroupActivity extends BaseActivity {
    @BindView(R.id.iv_avatar)
    ImageView mIvGroupAvatar;
    @BindView(R.id.layout_group_icon)
    LinearLayout mLayoutGroupIcon;
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox publibCheckBox;
    private CheckBox memberCheckbox;
    private TextView secondTextView;

    private EaseTitleBar mTitleBar;
    private IGroupModel mModel;

    private String avatarName;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_group);
        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void setListener() {
        publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondTextView.setText(R.string.join_need_owner_approval);
                } else {
                    secondTextView.setText(R.string.Open_group_members_invited);
                }
            }
        });
        mTitleBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(NewGroupActivity.this);
            }
        });

    }

    private void initView() {
        mModel = new GroupModel();
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        publibCheckBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        secondTextView = (TextView) findViewById(R.id.second_desc);
        mTitleBar = (EaseTitleBar) findViewById(R.id.ease_title_group);
        mTitleBar.setLeftImageResource(R.drawable.icon_back);
        mTitleBar.setTitle(getResources().getString(R.string.The_new_group_chat));
    }

    /**
     * @param v
     */
    public void save(View v) {
        String name = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
        } else {
            // select from contact list
            startActivityForResult(new Intent(this, GroupPickContactsActivity.class)
                    .putExtra("groupName", name), I.REQUEST_CODE_PICK_CONTACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        if (resultCode == RESULT_OK) {
//            //new group
//            showDialog();
//            createEMGroup(data);
//        }
        switch (requestCode) {
            case I.REQUEST_CODE_PICK_PIC:
                if (data == null && data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case I.REQUEST_CODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            case I.REQUEST_CODE_PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    //new group
                    showDialog();
                    createEMGroup(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createEMGroup(final Intent data) {
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                try {
                    EMGroupOptions option = new EMGroupOptions();
                    option.maxUsers = 200;
                    option.inviteNeedConfirm = true;

                    String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
                    reason = EMClient.getInstance().getCurrentUser() + reason + groupName;

                    if (publibCheckBox.isChecked()) {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                    EMGroup emGroup = EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
                    createGroup(emGroup);

                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void createGroup(EMGroup emGroup) {
        if (emGroup != null) {
            File file = null;
            mModel.newGroup(NewGroupActivity.this, emGroup.getGroupId(), emGroup.getGroupName(),
                    emGroup.getDescription(), emGroup.getOwner(), emGroup.isPublic(), emGroup.isAllowInvites(),
                    file, new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            boolean success = false;
                            if (s != null) {
                                Result result = ResultUtils.getResultFromJson(s, Group.class);
                                if (result != null && result.isRetMsg()) {
                                    Group group = (Group) result.getRetData();
                                    if (group != null) {
                                        success = true;
                                    }
                                }
                            }
                            createSucces(success);
                        }

                        @Override
                        public void onError(String error) {
                            createSucces(false);
                        }
                    });
        }
    }

    private void createSucces(final boolean success) {
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(NewGroupActivity.this, R.string.Failed_to_create_groups, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showDialog() {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    @OnClick(R.id.layout_group_icon)
    public void onClick() {

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, I.REQUEST_CODE_CUTTING);
    }
    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            mIvGroupAvatar.setImageDrawable(drawable);
            saveBitmapFile(photo);
        }
    }

    private void saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(NewGroupActivity.this,I.AVATAR_TYPE)+"/"+getAvatarName()+".jpg";
            File file = new File(imagePath);//将要保存图片的路径
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFile =  file;
        }
    }
    private String getAvatarName() {
        avatarName = I.AVATAR_TYPE_GROUP_PATH+ System.currentTimeMillis();
        return avatarName;
    }
    /**
     * 返回头像保存在sd卡的位置:
     * Android/data/cn.ucai.superwechat/files/pictures/user_avatar
     * @param context
     * @param path
     * @return
     */
    public static String getAvatarPath(Context context, String path){
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File folder = new File(dir,path);
        if(!folder.exists()){
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }
}
