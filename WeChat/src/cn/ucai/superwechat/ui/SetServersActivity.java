package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import cn.ucai.superwechat.SuperWeChatModel;
import cn.ucai.superwechat.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class SetServersActivity extends BaseActivity {

    EditText restEdit;
    EditText imEdit;
    EaseTitleBar titleBar;

    SuperWeChatModel mSuperWeChatModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_servers);

        restEdit = (EditText) findViewById(R.id.et_rest);
        imEdit = (EditText) findViewById(R.id.et_im);
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);

        mSuperWeChatModel = new SuperWeChatModel(this);
        if(mSuperWeChatModel.getRestServer() != null)
            restEdit.setText(mSuperWeChatModel.getRestServer());
        if(mSuperWeChatModel.getIMServer() != null)
            imEdit.setText(mSuperWeChatModel.getIMServer());
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(!TextUtils.isEmpty(restEdit.getText()))
            mSuperWeChatModel.setRestServer(restEdit.getText().toString());
        if(!TextUtils.isEmpty(imEdit.getText()))
            mSuperWeChatModel.setIMServer(imEdit.getText().toString());
        super.onBackPressed();
    }
}
