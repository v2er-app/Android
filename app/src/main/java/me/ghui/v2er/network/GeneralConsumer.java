package me.ghui.v2er.network;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.network.bean.IBase;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.TwoStepLoginInfo;
import me.ghui.v2er.util.RxUtils;
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
            /*
             Possible Reasons:
                1. need login but no login
                2. need login but login is expired
                3. no premission to open the page
            */
            GeneralError generalError = new GeneralError(ResultCode.NETWORK_ERROR, "Unknown Error");
            String response = t.getResponse();
            generalError.setResponse(response);
            Observable.just(response)
                    .compose(RxUtils.io_main())
                    .map(s -> {
                        BaseInfo resultInfo = APIService.fruit().fromHtml(s, LoginParam.class);
                        if (resultInfo == null) return null;
                        if (!resultInfo.isValid()) {
                            resultInfo = APIService.fruit().fromHtml(s, NewsInfo.class);
                        }
                        if (!resultInfo.isValid()) {
                            resultInfo = APIService.fruit().fromHtml(s, TwoStepLoginInfo.class);
                        }
                        // 31/07/2017 More tries...
                        return resultInfo;
                    })
                    .subscribe(resultInfo -> {
                        if (resultInfo == null || !resultInfo.isValid()) {
                            onError(generalError);
                            return;
                        }
                        if (resultInfo instanceof LoginParam) {
                            if (UserUtils.isLogin()) {
                                generalError.setErrorCode(ResultCode.LOGIN_EXPIRED);
                                generalError.setMessage("登录已过期，请重新登录");
                            } else {
                                generalError.setErrorCode(ResultCode.LOGIN_NEEDED);
                                generalError.setMessage("需要您先去登录");
                            }
                        } else if (resultInfo instanceof NewsInfo) {
                            generalError.setErrorCode(ResultCode.REDIRECT_TO_HOME);
                            generalError.setMessage("Redirecting to home");
                        } else if (resultInfo instanceof TwoStepLoginInfo) {
                            generalError.setErrorCode(ResultCode.LOGIN_TWO_STEP);
                            generalError.setMessage("Two Step Login");
                        }
                        onError(generalError);
                    });

        }
    }

    public abstract void onConsume(T t);

    @Override
    public void onError(Throwable e) {
        if (mGeneralErrorHandler == null) {
            Voast.show(e.getMessage());
        } else {
            GeneralError generalError;
            if (e instanceof GeneralError) {
                generalError = (GeneralError) e;
            } else {
                generalError = new GeneralError(ResultCode.NETWORK_ERROR, e.getMessage());
            }
            mGeneralErrorHandler.handleError(generalError);
        }
    }

    @Override
    public void onComplete() {

    }
}
