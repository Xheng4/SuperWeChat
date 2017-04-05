package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_profile);
        ButterKnife.bind(this);
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
        User user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            MFGT.finish(SearchUserProfileActivity.this);
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
    }

    private void showFirend(boolean isFriend) {
        mSearchAddBtn.setVisibility(isFriend?View.GONE:View.VISIBLE);
        mSearchVedioBtn.setVisibility(isFriend ? View.VISIBLE : View.GONE);
        mSearchSendBtn.setVisibility(isFriend ? View.VISIBLE : View.GONE);
    }

//    /**
//     *  add contact
//     * @param view
//     */
//    public void addContact(View view) {
//        if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
//            new EaseAlertDialog(this, R.string.not_add_myself).show();
//            return;
//        }
//
//        if (SuperWeChatHelper.getInstance().getContactList().containsKey(nameText.getText().toString())) {
//            //let the user know the contact already in your contact list
//            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
//                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
//                return;
//            }
//            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
//            return;
//        }
//
//        progressDialog = new ProgressDialog(this);
//        String stri = getResources().getString(R.string.Is_sending_a_request);
//        progressDialog.setMessage(stri);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        new Thread(new Runnable() {
//            public void run() {
//
//                try {
//                    //demo use a hardcode reason here, you need let user to input if you like
//                    String s = getResources().getString(R.string.Add_a_friend);
//                    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            String s1 = getResources().getString(R.string.send_successful);
//                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } catch (final Exception e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
//                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            }
//        }).start();
//    }

    @OnClick({R.id.search_mark_ll, R.id.search_add_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_mark_ll:
                break;
            case R.id.search_add_btn:
                break;
        }
    }
}
