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

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.model.bean.Result;
import cn.ucai.superwechat.model.net.IUserModel;
import cn.ucai.superwechat.model.net.OnCompleteListener;
import cn.ucai.superwechat.model.net.UserModel;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.ResultUtils;

import com.hyphenate.exceptions.HyphenateException;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * register screen
 */
public class RegisterActivity extends BaseActivity {
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    private EditText mNick;

    IUserModel mModel;
    String username, pwd, nick;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        mModel = new UserModel();
        initView();
    }

    private void initView() {
        userNameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
        mNick = (EditText) findViewById(R.id.register_nick);
    }

    public void register(View view) {
        if (!checkEdit()) {
            return;
        }
        registerWeChat();

    }

    private void registerWeChat() {
        Log.e("register", "registerWeChat()");
        pDialog();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
                mModel.register(RegisterActivity.this, username, nick, MD5.getMessageDigest(pwd),
                        new OnCompleteListener<String>() {
                            @Override
                            public void onSuccess(String result) {
                                if (result != null) {
                                    Result json = ResultUtils.getResultFromJson(result, String.class);
                                    if (json != null) {
                                        if (json.isRetMsg()) {
                                            Log.e("register", "成功？");
                                            registerEm();
                                        } else if (json.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                            Log.e("register", "用户已存在");
                                            CommonUtils.showShortToast(R.string.User_already_exists);
                                        } else if (json.getRetCode() == I.MSG_REGISTER_FAIL) {
                                            Log.e("register", "注册失败");
                                            CommonUtils.showShortToast(R.string.Registration_failed);
                                        }
                                    }
                                }
                                pd.dismiss();
                            }

                            @Override
                            public void onError(String error) {
                                CommonUtils.showShortToast(R.string.Registration_failed);
                                pd.dismiss();
                            }
                        });
//            }
//        }.start();

    }

    private void registerEm() {
        pDialog();

        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(pwd));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // save current user
                            SuperWeChatHelper.getInstance().setCurrentUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    unRegisterWeChat();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.network_anomalies),
                                        Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.User_already_exists),
                                        Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.registration_failed_without_permission),
                                        Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.illegal_user_name),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.Registration_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void unRegisterWeChat() {
        mModel.unRegister(RegisterActivity.this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Log.e("register", "unRegisterWeChat");
                }
            }

            @Override
            public void onError(String error) {
                Log.e("register", "unRegisterWeChat-error:"+error);
            }
        });
    }

    private void pDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.Is_the_registered));
        pd.show();
    }

    private boolean checkEdit() {
        username = userNameEditText.getText().toString().trim();
        pwd = passwordEditText.getText().toString().trim();
        nick = mNick.getText().toString().trim();

        String confirm_pwd = confirmPwdEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return false;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(nick)) {
            Toast.makeText(this, getResources().getString(R.string.nick_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    public void back(View view) {
        finish();
    }

}
