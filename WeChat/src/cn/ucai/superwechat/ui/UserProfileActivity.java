package cn.ucai.superwechat.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.L;

public class UserProfileActivity extends BaseActivity {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    @BindView(R.id.user_head_avatar)
    ImageView mUserHeadAvatar;
    @BindView(R.id.user_head_headphoto_update)
    ImageView mUserHeadHeadphotoUpdate;
    @BindView(R.id.tv_nick)
    TextView mTvNick;
    @BindView(R.id.tv_account)
    TextView mTvAccount;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_area)
    TextView mTvArea;
    @BindView(R.id.tv_signature)
    TextView mTvSignature;
    private ProgressDialog dialog;

    UpdateNickReceiver mReceiver;
    UpdateAvatarReceiver mReceiverAvatar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mReceiver = new UpdateNickReceiver();
        mReceiverAvatar = new UpdateAvatarReceiver();

        IntentFilter filter = new IntentFilter(I.REQUEST_UPDATE_USER_NICK);
        IntentFilter filter1 = new IntentFilter(I.REQUEST_UPDATE_AVATAR);

        registerReceiver(mReceiver, filter);
        registerReceiver(mReceiverAvatar, filter1);
    }

    private void initView() {
//		headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
//		headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
//		tvUsername = (TextView) findViewById(R.id.user_username);
//		tvNickName = (TextView) findViewById(R.id.user_nickname);
//		rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
//		iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);
    }

    private void initData() {
        Intent intent = getIntent();
        String username = EMClient.getInstance().getCurrentUser();
//        String username = intent.getStringExtra("username");
        boolean enableUpdate = intent.getBooleanExtra("setting", false);
        if (enableUpdate) {
            mUserHeadHeadphotoUpdate.setVisibility(View.VISIBLE);
        } else {
            mUserHeadHeadphotoUpdate.setVisibility(View.GONE);
        }
        if (username != null) {
            if (username.equals(EMClient.getInstance().getCurrentUser())) {
                mTvAccount.setText(EMClient.getInstance().getCurrentUser());
                EaseUserUtils.setWeChatUserNick(username, mTvNick);
                EaseUserUtils.setWeChatUserAvatar(this, username, mUserHeadAvatar);
            } else {
                mTvAccount.setText(username);
                EaseUserUtils.setUserNick(username, mTvNick);
                EaseUserUtils.setUserAvatar(this, username, mUserHeadAvatar);
                asyncFetchUserInfo(username);
            }
        }
    }

    public void asyncFetchUserInfo(String username) {
        SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser user) {
                if (user != null) {
                    SuperWeChatHelper.getInstance().saveContact(user);
                    if (isFinishing()) {
                        return;
                    }
                    mTvNick.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(mUserHeadAvatar);
                    } else {
                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(mUserHeadAvatar);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo),
                        getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                L.e("avatar", "图片选则意图");
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
    }

    private void updateReNick(boolean b) {
        if (!b) {
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                    .show();
            dialog.dismiss();

        } else {
            dialog.dismiss();
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                    .show();
            User user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentWeChatUserInfo();
            mTvNick.setText(user.getMUserNick());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                L.e("avatar", "图片裁剪");
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                    L.e("avatar", "图片准备保存");
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    private void savePhotoPath() {
//        File file = new File();
//        File fileDir = new File(Environment.getExternalStorageDirectory(), strFileDir);  //定义目录
//        if (!fileDir.exists()) {   //判断目录是否存在
//            Log.e("FileTool",fileDir.getPath());
//            fileDir.mkdirs();      //如果不存在则先创建目录
//        }
//        strFileName = strFileName  + System.currentTimeMillis()+".jpg";
//        File file = new File(fileDir, strFileName);   //定义文件
//        if (!file.exists()) {  //判断文件是否存在
//            Log.e("FileTools",file.toString());
//            file.createNewFile();    //如果不存在则先创建文件
//        }
//
//    }
    public File getAvatarPath(String fielName) {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        dir = new File(dir, I.AVATAR_TYPE_USER_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fielName);
        L.e("avatar", "file:" + file);
        return file;
    }

    private String getAvatarName() {
        User user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentWeChatUserInfo();
        String avatarName = user.getMUserName() + System.currentTimeMillis() + ".png";
        Log.e("avatar", "avatarName:" + avatarName);
        return avatarName;
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
        startActivityForResult(intent, REQUESTCODE_CUTTING);
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
            mUserHeadAvatar.setImageDrawable(drawable);
            L.e("avatar", "获取到bitmap");
            uploadUserAvatar(Bitmap2File(photo));
        }

    }
//    /**
//     * 保存文件
//     * @param bm
//     * @param fileName
//     * @throws IOException
//     */
//    public void saveFile(Bitmap bm, String fileName) {
//        String path = getSDPath() +"/revoeye/";
//        File dirFile = new File(path);
//        if(!dirFile.exists()){
//            dirFile.mkdir();
//        }
//        File myCaptureFile = new File(path + fileName);
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//
//    }

    private File Bitmap2File(Bitmap bitmap) {
        File file = null;
        try {
            file = getAvatarPath(getAvatarName());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
            L.e("avatar", "Bitmap2File:" + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void uploadUserAvatar(final File file) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {
            @Override
            public void run() {
                SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(file);
            }
        }).start();

        dialog.show();
    }

    private void updateReAvatar(boolean success) {
        dialog.dismiss();
        if (success) {
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                    Toast.LENGTH_SHORT).show();
            User user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentWeChatUserInfo();
            EaseUserUtils.setWeChatUserAvatar(UserProfileActivity.this, user.getMUserName(), mUserHeadAvatar);
        } else {
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                    Toast.LENGTH_SHORT).show();
        }
    }


//    public byte[] Bitmap2Bytes(Bitmap bm) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//    }

    @OnClick({R.id.rl_nickname, R.id.rl_account, R.id.ic_right_arrow, R.id.rl_address,
            R.id.rl_sex, R.id.rl_area, R.id.rl_signature, R.id.rl_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_nickname:
                View inflate = View.inflate(UserProfileActivity.this, R.layout.popup_update_nick, null);
                final EditText editText = (EditText) inflate.findViewById(R.id.et_update_nick);
                TextView textView = (TextView) findViewById(R.id.tv_update_title);
                new Builder(this)
                        .setCustomTitle(textView)
                        .setView(inflate)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = editText.getText().toString().trim();
                                if (TextUtils.isEmpty(s)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(s);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            case R.id.rl_account:
                break;
            case R.id.ic_right_arrow:
                break;
            case R.id.rl_address:
                break;
            case R.id.rl_sex:
                break;
            case R.id.rl_area:
                break;
            case R.id.rl_signature:
                break;
            case R.id.rl_avatar:
                uploadHeadPhoto();
                L.e("avatar", "开始");
                break;
        }
    }

    class UpdateNickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean booleanExtra = intent.getBooleanExtra(I.User.NICK, false);
            updateReNick(booleanExtra);
        }
    }

    class UpdateAvatarReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(I.Avatar.AVATAR_ID, false);
            updateReAvatar(success);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
