package me.ghui.v2er.module.login;


import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.IBaseInfo;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.network.bean.MissionInfo;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 27/03/2017.
 */

public class LoginPresenter implements LoginContract.IPresenter {

    private LoginContract.IView mView;
    private LoginParam mLoginParam;

    public LoginPresenter(LoginContract.IView mView) {
        this.mView = mView;
    }

    @Override
    public void start() {
        APIService.get().loginParam()
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<LoginParam>() {
                    @Override
                    public void onConsume(LoginParam loginParam) {
                        if (loginParam.isValid()) {
                            mLoginParam = loginParam;
                            mView.onFetchLoginParamSuccess();
                        } else {
                            mView.onFetchLoginParamFailure();
                        }
                    }
                });
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void login(String userName, String psw) {
        APIService.get().login(mLoginParam.toMap(userName, psw))
                .compose(mView.rx())
                .map(response -> response.body().string())
                .map(s -> {
                    MissionInfo resultInfo = APIService.fruit().fromHtml(s, MissionInfo.class);
                    if (!resultInfo.isValid()) {
                        return APIService.fruit().fromHtml(s, LoginParam.class);
                    }
                    return resultInfo;
                })
                .subscribe(new GeneralConsumer<IBaseInfo>() {
                    @Override
                    public void onConsume(IBaseInfo info) {
                        if (info instanceof MissionInfo) {
                            //login success
                            MissionInfo resultInfo = (MissionInfo) info;
                            UserUtils.saveLogin(UserInfo.build(resultInfo.getUserName(), resultInfo.getAvatar()));
                            mView.onLoginSuccess();
                        } else {
                            //login failure
                            LoginParam loginParam = (LoginParam) info;
                            if (loginParam.isValid()) {
                                mLoginParam = loginParam;
                                mView.onLoginFailure("登录失败，用户名和密码无法匹配");
                            } else {
                                mView.onLoginFailure(App.get().getString(R.string.login_occur_unknown_error));
                            }
                        }
                    }
                });
    }

}
