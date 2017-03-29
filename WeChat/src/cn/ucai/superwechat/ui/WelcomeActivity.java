package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.widget.MFGT;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login_wel, R.id.btn_register_wel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_wel:
                MFGT.gotoLogin(WelcomeActivity.this);

                break;
            case R.id.btn_register_wel:
                MFGT.gotoRegister(WelcomeActivity.this);

                break;
        }
    }
}
