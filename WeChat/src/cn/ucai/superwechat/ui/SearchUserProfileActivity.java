package cn.ucai.superwechat.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.db.InviteMessgeDao;
import cn.ucai.superwechat.domain.InviteMessage;
import cn.ucai.superwechat.model.bean.Result;
import cn.ucai.superwechat.model.net.IUserModel;
import cn.ucai.superwechat.model.net.OnCompleteListener;
import cn.ucai.superwechat.model.net.UserModel;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.MFGT;

public class SearchUserProfileActivity extends BaseActivity {

    @BindView(R.id.search_avatar_user)
    ImageView mSearchAvatarUser;
    @BindView(R.id.search_nick_user)
    TextView mSearchNickUser;
    @BindView(R.id.search_account_user)
    TextView mSearchAccountUser;
    @BindView(R.id.search_add_btn)
    Button mSearchAddBtn;
    @BindView(R.id.search_send_btn)
    Button mSearchSendBtn;
    @BindView(R.id.search_vedio_btn)
    Button mSearchVedioBtn;

    EaseTitleBar mTitleBar;
    User user;
    InviteMessage msg;

    IUserModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_profile);
        ButterKnife.bind(this);

        mModel = new UserModel();
        initView();
        initData();
    }

    private void initView() {
        mTitleBar = (EaseTitleBar) findViewById(R.id.search_title_bar);
        mTitleBar.setLeftImageResource(R.drawable.ease_mm_title_back);
        mTitleBar.setTitle(getResources().getString(R.string.title_friend_profile));

        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(SearchUserProfileActivity.this);
            }
        });
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            msg = (InviteMessage) getIntent().getSerializableExtra("msg");
            if (msg != null) {
                user = new User(msg.getFrom());
                user.setMUserNick(msg.getNick());
                user.setAvatar(msg.getAvatar());
                showInfo(user);
            } else {
                MFGT.finish(SearchUserProfileActivity.this);
            }
        } else {
            showInfo(user);
        }
    }

    private void showInfo(User user) {
        boolean isFriend = SuperWeChatHelper.getInstance()
                .getWeChatContactList().containsKey(user.getMUserName());
        if (isFriend) {
            SuperWeChatHelper.getInstance().saveWeChatContact(user);
        }
        mSearchAccountUser.setText(user.getMUserName());
//        mSearchNickUser.setText(user.getMUserNick());
        EaseUserUtils.setWeChatUserNick(user, mSearchNickUser);
        EaseUserUtils.setWeChatUserAvatar(SearchUserProfileActivity.this, user, mSearchAvatarUser);
        showFirend(isFriend);
        syncUserInfo();
    }

    private void syncUserInfo() {
        mModel.loadUserInfo(SearchUserProfileActivity.this, user.getMUserName(), new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Result json = ResultUtils.getResultFromJson(result, User.class);
                    if (json != null && json.isRetMsg()) {
                        User u = (User) json.getRetData();
                        if (u != null && msg != null) {
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_NICK, u.getMUserNick());
                            values.put(InviteMessgeDao.COLUMN_NAME_AVATAR, u.getAvatar());
                            InviteMessgeDao dao = new InviteMessgeDao(SearchUserProfileActivity.this);
                            dao.updateMessage(msg.getId(), values);
                        } else {
                            SuperWeChatHelper.getInstance().saveWeChatContact(u);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showFirend(boolean isFriend) {
        mSearchAddBtn.setVisibility(isFriend?View.GONE:View.VISIBLE);
        mSearchVedioBtn.setVisibility(isFriend ? View.VISIBLE : View.GONE);
        mSearchSendBtn.setVisibility(isFriend ? View.VISIBLE : View.GONE);
    }



    @OnClick({R.id.search_mark_ll, R.id.search_add_btn,R.id.search_send_btn,R.id.search_vedio_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_mark_ll:
                break;
            case R.id.search_send_btn:
                finish();
                MFGT.gotoChat(SearchUserProfileActivity.this,user.getMUserName());
                break;
            case R.id.search_add_btn:
                boolean isCheck = true;
                if (isCheck) {
                    MFGT.gotoAddFriendCheck(SearchUserProfileActivity.this, user.getMUserName());
                } else {
                    //直接添加
                }
                break;
            case R.id.search_vedio_btn:
                startVideoCall();
                break;
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(SearchUserProfileActivity.this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(SearchUserProfileActivity.this, VideoCallActivity.class).putExtra("username", user.getMUserName())
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
//            inputMenu.hideExtendMenuContainer();
        }
    }
}
