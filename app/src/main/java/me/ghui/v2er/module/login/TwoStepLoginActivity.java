package me.ghui.v2er.module.login;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.tencent.bugly.crashreport.CrashReport;

import org.jsoup.helper.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.LoginResultInfo;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 16/08/2017.
 */

public class TwoStepLoginActivity extends BaseActivity implements ClipboardManager.OnPrimaryClipChangedListener {
    private static String KEY_TWO_STEP_LOGIN_ONCE = KEY("two_step_login_once");
    @BindView(R.id.login_code_text_input_layout)
    TextInputLayout mTextInputLayout;
    @BindView(R.id.positive_btn)
    Button mPositiveBtn;
    private String mOnce;
    private ClipboardManager mClipboardManager;

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
    protected boolean supportSlideBack() {
        return false;
    }

    @Override
    protected void initTheme() {
        switch (DayNightUtil.getMode()) {
            case DayNightUtil.NIGHT_MODE:
                setTheme(R.style.NightDialogTheme);
                break;
            case DayNightUtil.AUTO_MODE:
                break;
            case DayNightUtil.DAY_MODE:
            default:
                setTheme(R.style.DialogTheme);
                break;
        }
    }

    @Override
    protected void init() {
        super.init();
        setFinishOnTouchOutside(false);
        mRootView.setBackgroundColor(getResources().getColor(R.color.transparent));
        mTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Check.isEmpty(getInput())) {
                    mPositiveBtn.setText("去复制");
                } else {
                    mPositiveBtn.setText("验证");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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

    public String getInput() {
        return mTextInputLayout.getEditText().getText().toString();
    }

    private void startMonitor() {
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }
        mClipboardManager.addPrimaryClipChangedListener(this);
    }

    @Override
    public void onPrimaryClipChanged() {
        if (mClipboardManager.hasPrimaryClip()) {
            CharSequence text = mClipboardManager.getPrimaryClip().getItemAt(0).getText();
            if (Check.notEmpty(text) && StringUtil.isNumeric(text.toString()) && text.length() == 6) {
                mTextInputLayout.getEditText().setText(text);
                mTextInputLayout.getEditText().setSelection(text.length());
            }
        }
    }

    @OnClick(R.id.positive_btn)
    void onPositiveBtnClicked() {
        String input = getInput();
        if (Check.isEmpty(input)) {
            Utils.openApp(this, "com.google.android.apps.authenticator2");
            startMonitor();
            return;
        }
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(this);
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
                                        toast("登录成功");
                                        UserUtils.saveLogin(UserInfo.build(resultInfo.getUserName(), resultInfo.getAvatar()));
                                        CrashReport.setUserId(resultInfo.getUserName());
                                        finish();
                                        Navigator.from(TwoStepLoginActivity.this)
                                                .setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .to(MainActivity.class).start();
                                    }
                                });
                    }
                });
    }


}
