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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.model.bean.Result;
import cn.ucai.superwechat.model.net.IUserModel;
import cn.ucai.superwechat.model.net.OnCompleteListener;
import cn.ucai.superwechat.model.net.UserModel;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.MFGT;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.widget.EaseAlertDialog;

public class AddContactActivity extends BaseActivity {
    private EditText editText;
    private RelativeLayout searchedUserLayout;
    private TextView nameText, mTextView, tvNoSearch;
    private Button searchBtn;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    IUserModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        initView();

        mModel = new UserModel();
    }

    private void initView() {
        searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
        mTextView = (TextView) findViewById(R.id.add_list_friends);
        editText = (EditText) findViewById(R.id.edit_note);
        nameText = (TextView) findViewById(R.id.name);
        tvNoSearch = (TextView) findViewById(R.id.tv_no_search_result);
        searchBtn = (Button) findViewById(R.id.search);

        String strAdd = getResources().getString(R.string.menu_addfriend);
        mTextView.setText(strAdd);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
    }


    /**
     * search contact
     * @param v
     */
    public void searchContact(View v) {
        final String name = editText.getText().toString();
        String saveText = searchBtn.getText().toString();

        if (getString(R.string.button_search).equals(saveText)) {
            toAddUsername = name;
            if (TextUtils.isEmpty(name)) {
                new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
                return;
            }

            // TODO you can search the user from your app server here.

            searchUser(name);

            //show the userame and add button if user exist
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);

        }
    }

    private void searchUser(String name) {
        mModel.loadUserInfo(AddContactActivity.this, name, new OnCompleteListener<String>() {
            boolean success = false;

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Result json = ResultUtils.getResultFromJson(result, User.class);
                    if (json != null && json.isRetMsg()) {
                        User user = (User) json.getRetData();
                        if (user != null) {

                            success = true;
                        }
                    }
                }
                displayResult(success);
            }

            @Override
            public void onError(String error) {
                displayResult(success);
            }
        });
    }

    private void displayResult(boolean b) {
        if (!b) {
            tvNoSearch.setText("你可能找了个假的用户...");
            tvNoSearch.setVisibility(View.VISIBLE);
        } else {
            tvNoSearch.setVisibility(View.GONE);

        }
    }

    /**
     *  add contact
     * @param view
     */
    public void addContact(View view) {
        if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (SuperWeChatHelper.getInstance().getContactList().containsKey(nameText.getText().toString())) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
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
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
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

    public void back(View v) {
        MFGT.finishZ(AddContactActivity.this);
    }
}
