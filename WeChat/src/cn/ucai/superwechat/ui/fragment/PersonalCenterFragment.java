package cn.ucai.superwechat.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.widget.MFGT;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCenterFragment extends Fragment {


    @BindView(R.id.iv_avatar_pc)
    ImageView mIvAvatarPc;
    @BindView(R.id.tv_nick_pc)
    TextView mTvNickPc;
    @BindView(R.id.tv_username_pc)
    TextView mTvUsernamePc;

    public PersonalCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        String username = EMClient.getInstance().getCurrentUser();
        if (username != null) {
            mTvUsernamePc.setText(username);
            EaseUserUtils.setWeChatUserNick(username, mTvNickPc);
            EaseUserUtils.setWeChatUserAvatar(getContext(), username, mIvAvatarPc);
        }
    }

    @OnClick({R.id.pc_profile_center, R.id.pc_photo_item, R.id.pc_collect_item, R.id.pc_money_item, R.id.pc_smail_item, R.id.pc_setting_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pc_profile_center:
                MFGT.gotoUserProfileActivity((Activity) getContext());
                break;
            case R.id.pc_photo_item:
                break;
            case R.id.pc_collect_item:
                break;
            case R.id.pc_money_item:
                RedPacketUtil.startChangeActivity(getContext());
                break;
            case R.id.pc_smail_item:
                break;
            case R.id.pc_setting_item:
                MFGT.gotoSettingsActivity((Activity)getContext());
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }
}
