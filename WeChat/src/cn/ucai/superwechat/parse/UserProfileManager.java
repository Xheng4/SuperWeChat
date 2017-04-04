package cn.ucai.superwechat.parse;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.SuperWeChatHelper.DataSyncListener;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.model.bean.Result;
import cn.ucai.superwechat.model.net.IUserModel;
import cn.ucai.superwechat.model.net.OnCompleteListener;
import cn.ucai.superwechat.model.net.UserModel;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.utils.ResultUtils;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */
	private List<DataSyncListener> syncContactInfosListeners;

	private boolean isSyncingContactInfosWithServer = false;

	private EaseUser currentUser;
	private User mUser;
	IUserModel mModel;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
		ParseManager.getInstance().onInit(context);
		syncContactInfosListeners = new ArrayList<DataSyncListener>();
		mModel = new UserModel();
		sdkInited = true;
		appContext = context;
		return true;
	}

	public void addSyncContactInfoListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.add(listener);
		}
	}

	public void removeSyncContactInfoListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.remove(listener);
		}
	}

	public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
		if (isSyncingContactInfosWithServer) {
			return;
		}
		isSyncingContactInfosWithServer = true;
		ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<EaseUser>>() {

			@Override
			public void onSuccess(List<EaseUser> value) {
				isSyncingContactInfosWithServer = false;
				// in case that logout already before server returns,we should
				// return immediately
				if (!SuperWeChatHelper.getInstance().isLoggedIn()) {
					return;
				}
				if (callback != null) {
					callback.onSuccess(value);
				}
			}

			@Override
			public void onError(int error, String errorMsg) {
				isSyncingContactInfosWithServer = false;
				if (callback != null) {
					callback.onError(error, errorMsg);
				}
			}

		});

	}

	public void notifyContactInfosSyncListener(boolean success) {
		for (DataSyncListener listener : syncContactInfosListeners) {
			listener.onSyncComplete(success);
		}
	}

	public boolean isSyncingContactInfoWithServer() {
		return isSyncingContactInfosWithServer;
	}

	public synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentUser = null;
		PreferenceManager.getInstance().removeCurrentUserInfo();
	}

	public synchronized EaseUser getCurrentUserInfo() {
		if (currentUser == null) {
			String username = EMClient.getInstance().getCurrentUser();
			currentUser = new EaseUser(username);
			String nick = getCurrentUserNick();
			currentUser.setNick((nick != null) ? nick : username);
			currentUser.setAvatar(getCurrentUserAvatar());
		}
		return currentUser;
	}

	public synchronized User getCurrentWeChatUserInfo() {
		if (mUser == null || mUser.getMUserName() == null) {
			String username = EMClient.getInstance().getCurrentUser();
			mUser = new User(username);
			String nick = getCurrentUserNick();
			mUser.setMUserNick((nick != null) ? nick : username);
			mUser.setAvatar(getCurrentUserAvatar());
		}
		return mUser;
	}


	public boolean updateCurrentUserNickName(final String nickname) {
		mModel.upDateNick(appContext, EMClient.getInstance().getCurrentUser(), nickname,
				new OnCompleteListener<String>() {
					Boolean b = false;
			@Override
			public void onSuccess(String res) {

				if (res != null) {
					Result result = ResultUtils.getResultFromJson(res, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
						if (user != null) {
							b = true;
							setCurrentWeChatUserNick(user.getMUserNick());
							SuperWeChatHelper.getInstance().saveWeChatContact(user);
						}
					}
				}
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK).putExtra(I.User.NICK,b));
			}

			@Override
			public void onError(String error) {
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK).putExtra(I.User.NICK,b));
			}
		});

		return false;
	}

	public boolean uploadUserAvatar(File file) {
		final boolean[] success = {false};
		L.e("avatar","uploadUserAvatar："+file);
		mModel.updateAvatar(appContext, EMClient.getInstance().getCurrentUser(), file , new OnCompleteListener<String>() {
			@Override
			public void onSuccess(String result) {
				L.e("avatar","uploadUserAvatar-result："+result);
				if (result != null) {
					Result json = ResultUtils.getResultFromJson(result, String.class);
					if (json != null && json.isRetMsg()) {
						User user = (User) json.getRetData();
						L.e("avatar","json.getRetData():"+user.toString());
						if (user != null) {
							setCurrentWeChatUserAvatar(user.getAvatar());
							SuperWeChatHelper.getInstance().saveWeChatContact(user);
							success[0] = true;

						}
					}
				}
//				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
//						.putExtra(I.Avatar.AVATAR_ID,success));
			}

			@Override
			public void onError(String error) {
				L.e("avatar","onError():"+error);
				success[0] = false;
//				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
//						.putExtra(I.Avatar.AVATAR_ID,success));
			}
		});

		return success[0];
	}

//	public void asyncGetCurrentUserInfo() {
//		ParseManager.getInstance().asyncGetCurrentUserInfo(new EMValueCallBack<EaseUser>() {
//
//			@Override
//			public void onSuccess(EaseUser value) {
//			    if(value != null){
//    				setCurrentUserNick(value.getNick());
//    				setCurrentUserAvatar(value.getAvatar());
//			    }
//			}
//
//			@Override
//			public void onError(int error, String errorMsg) {
//
//			}
//		});
//	}

	public void asyncGetCurrentWeChatUserInfo() {
		L.e("asyncGetCurrentWeChatUserInfo");
		mModel.loadUserInfo(appContext, EMClient.getInstance().getCurrentUser(), new OnCompleteListener<String>() {
			@Override
			public void onSuccess(String result) {
				if (result != null) {
					Result json = ResultUtils.getResultFromJson(result, User.class);
					if (json != null && json.isRetMsg()) {
						User user = (User) json.getRetData();
						if (user != null) {
							L.e("loadUserInfo");
							L.e("loadUserInfo","user:"+user.toString());
							mUser = user;
							setCurrentWeChatUserNick(user.getMUserNick());
							setCurrentWeChatUserAvatar(user.getAvatar());
							SuperWeChatHelper.getInstance().saveWeChatContact(user);
						}
					}
				}
			}

			@Override
			public void onError(String error) {
				L.e(error);
			}
		});
	}

	public void asyncGetUserInfo(final String username,final EMValueCallBack<EaseUser> callback){
		ParseManager.getInstance().asyncGetUserInfo(username, callback);
	}
	private void setCurrentUserNick(String nickname) {
		getCurrentUserInfo().setNick(nickname);
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}


//	private void setCurrentUserAvatar(String avatar) {
//		getCurrentUserInfo().setAvatar(avatar);
//		PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
//	}

	private void setCurrentWeChatUserNick(String nickname) {
		getCurrentWeChatUserInfo().setMUserNick(nickname);
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentWeChatUserAvatar(String avatar) {
		L.e("loadUserInfo","avatar:"+avatar);
		getCurrentWeChatUserInfo().setAvatar(avatar);
		PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
	}


	private String getCurrentUserNick() {
		return PreferenceManager.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return PreferenceManager.getInstance().getCurrentUserAvatar();
	}

}
