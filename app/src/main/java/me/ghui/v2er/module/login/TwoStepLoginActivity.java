package me.ghui.v2er.module.login;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.LoginResultInfo;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 16/08/2017.
 */

public class TwoStepLoginActivity extends BaseActivity {
    private static String KEY_TWO_STEP_LOGIN_ONCE = KEY("two_step_login_once");
    private String mOnce;

    @BindView(R.id.login_code_text_input_layout)
    TextInputLayout mTextInputLayout;

    public static void open(String once, Context context) {
        Navigator.from(context)
                .putExtra(KEY_TWO_STEP_LOGIN_ONCE, once)
                .addFlag(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .to(TwoStepLoginActivity.class)
                .start();
    }

    @Override
    protected void parseExtras(Intent intent) {
        super.parseExtras(intent);
        mOnce = intent.getStringExtra(KEY_TWO_STEP_LOGIN_ONCE);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_two_step_login;
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void init() {
        super.init();
        setFinishOnTouchOutside(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseExtras(intent);
        toast("验证失败，请重试");
    }

    @OnClick(R.id.negative_btn)
    void onCancleBtnClicked() {
//        UserUtils.clearLogin();
        finish();
    }

    @OnClick(R.id.positive_btn)
    void onPositiveBtnClicked() {
        String input = mTextInputLayout.getEditText().getText().toString();
        if (PreConditions.isEmpty(input)) {
            toast("请输入验证码");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("once", mOnce);
        map.put("code", input);
        APIService.get()
                .signInTwoStep(map)
                .compose(rx())
                .subscribe(new GeneralConsumer<NewsInfo>(this) {
                    @Override
                    public void onConsume(NewsInfo resultInfo) {
                        Observable.just(resultInfo.getResponse())
                                .map(s -> APIService.fruit().fromHtml(s, LoginResultInfo.class))
                                .subscribe(new GeneralConsumer<LoginResultInfo>() {
                                    @Override
                                    public void onConsume(LoginResultInfo resultInfo) {
                                        UserUtils.saveLogin(UserInfo.build(resultInfo.getUserName(), resultInfo.getAvatar()));
                                        toast("登录成功");
                                        Navigator.from(TwoStepLoginActivity.this)
                                                .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .to(MainActivity.class).start();
                                        CrashReport.setUserId(resultInfo.getUserName());
                                        finish();
                                    }
                                });

                    }
                });
    }

}
