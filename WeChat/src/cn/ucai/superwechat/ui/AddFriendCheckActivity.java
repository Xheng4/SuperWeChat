package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.widget.MFGT;

public class AddFriendCheckActivity extends BaseActivity {

    @BindView(R.id.add_list_friends)
    TextView mAddListFriends;
    @BindView(R.id.btn_send_msg)
    Button mBtnSendMsg;
    @BindView(R.id.edit_msg)
    EditText mEditMsg;
    String friendName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_check);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mEditMsg.setText(getString(R.string.addcontact_send_msg_prefix)+
                SuperWeChatHelper.getInstance().getUserProfileManager()
                        .getCurrentWeChatUserInfo().getMUserNick());
        friendName = getIntent().getStringExtra(I.User.USER_NAME);
        if (friendName == null) {
            MFGT.finish(AddFriendCheckActivity.this);
        }
    }

    @OnClick(R.id.btn_send_msg)
    public void onClick() {
        if (friendName != null) {
            addContact();
        }
    }

    public void back(View v) {
        MFGT.finish(AddFriendCheckActivity.this);
    }


    /**
     *  add contact
     */
    public void addContact() {
        if (EMClient.getInstance().getCurrentUser().equals(friendName)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (SuperWeChatHelper.getInstance().getWeChatContactList().containsKey(friendName)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(friendName)) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = mEditMsg.getText().toString();
                    EMClient.getInstance().contactManager().addContact(friendName, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}
