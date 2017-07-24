package me.ghui.v2er.network;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.network.bean.IBase;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Voast;
import okhttp3.ResponseBody;

/**
 * Created by ghui on 19/06/2017.
 */

public abstract class GeneralConsumer<T extends IBase> implements Observer<T> {

    private IGeneralErrorHandler mGeneralErrorHandler;

    public GeneralConsumer(IGeneralErrorHandler generalErrorHandler) {
        mGeneralErrorHandler = generalErrorHandler;
    }

    public GeneralConsumer() {
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(T t) {
        Logger.d("API RESPONSE: \n" + t.toString() + "\n");
        if (t.isValid()) {
            onConsume(t);
        } else {
            String response = t.getResponse();
            // TODO: 23/07/2017 try to find the reason from rawResponse
            /*
             Possible Reasons:
                1. need login but no login
                2. need login but login is expired
                3. no premission to open the page
            */
            int errorCode = ResultCode.NETWORK_ERROR;
            String msg = null;
            LoginParam loginParam = APIService.fruit().fromHtml(response, LoginParam.class);
            if (loginParam.isValid()) {
                if (UserUtils.isLogin()) {
                    errorCode = ResultCode.LOGIN_EXPIRED;
                    msg = "登录已过期，请重新登录";
                } else {
                    errorCode = ResultCode.LOGIN_NEEDED;
                    msg = "需要您先去登录";
                }
            } else {
                // TODO: 24/07/2017 more case
            }
            onError(new GeneralError(errorCode, msg));
        }
    }

    public abstract void onConsume(T t);

    @Override
    public void onError(Throwable e) {
        if (mGeneralErrorHandler == null) {
            Voast.show(e.getMessage());
        } else {
            int errorCode;
            if (e instanceof GeneralError) {
                errorCode = ((GeneralError) e).getErrorCode();
            } else {
                errorCode = ResultCode.NETWORK_ERROR;
            }
            mGeneralErrorHandler.handleError(errorCode, e.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }
}
