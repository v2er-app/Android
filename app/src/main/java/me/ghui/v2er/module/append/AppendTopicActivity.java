package me.ghui.v2er.module.append;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.module.imgur.ImageUploadHelper;
import io.reactivex.Observable;
import me.ghui.v2er.R;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.injector.component.DaggerAppendTopicComponnet;
import me.ghui.v2er.injector.module.AppendTopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.GeneralError;
import me.ghui.v2er.network.bean.AppendTopicPageInfo;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

public class AppendTopicActivity extends BaseActivity<AppendTopicContract.IPresenter> implements AppendTopicContract.IView {
    public static final String KEY_TOPIC_ID = KEY("topic_id_key");
    private static final String KEY_PAGE_INFO = KEY("page_info");
    private static final String KEY_CONTENT = KEY("append_content");
    private String mTopicId;
    private AppendTopicPageInfo mPageInfo;

    @BindView(R.id.append_topic_content_et)
    EditText mContentET;
    @BindView(R.id.append_upload_image_btn)
    ImageButton mUploadImageBtn;
    private ImageUploadHelper mImageUploadHelper;


    public static void open(String topicId, Context context) {
        Navigator.from(context)
                .to(AppendTopicActivity.class)
                .putExtra(KEY_TOPIC_ID, topicId)
                .start();
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTopicId = intent.getStringExtra(KEY_TOPIC_ID);
        mPresenter.setTopicId(mTopicId);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_append_topic;
    }

    @Override
    protected void startInject() {
        DaggerAppendTopicComponnet.builder().appComponent(getAppComponent())
                .appendTopicModule(new AppendTopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        //设置右上角的填充菜单
        toolBar.inflateMenu(R.menu.append_topic_menu);
        Utils.setPaddingForStatusBar(toolBar);
        Utils.setPaddingForNavbar(mRootView);
        toolBar.setOnMenuItemClickListener(item -> {
            String content = mContentET.getText().toString();
            if (Check.isEmpty(content)) {
                Voast.show("请输入附言内容");
                return false;
            }
            mPresenter.sendAppend(content);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mContentET.getText().toString())) {
            new ConfirmDialog.Builder(this)
                    .title("丢弃附言")
                    .msg("返回将丢弃当前编写的内容")
                    .positiveText(R.string.ok, dialog -> finish())
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        mContentET.setText(intent.getStringExtra(KEY_CONTENT));
        mPageInfo = (AppendTopicPageInfo) intent.getSerializableExtra(KEY_PAGE_INFO);
        mTopicId = intent.getStringExtra(KEY_TOPIC_ID);

        // Initialize image upload helper
        mImageUploadHelper = new ImageUploadHelper(this, new ImageUploadHelper.OnUploadListener() {
            @Override
            public void onUploadStart() {
                showLoading();
                toast(R.string.uploading);
            }

            @Override
            public void onUploadSuccess(String imageLink) {
                hideLoading();
                insertImageLink(imageLink);
                toast(R.string.upload_success);
            }

            @Override
            public void onUploadFailed(String errorMsg) {
                hideLoading();
                toast(getString(R.string.upload_failed) + ": " + errorMsg);
            }
        });
    }

    @Override
    protected void autoLoad() {
        if (mPageInfo == null) {
            super.autoLoad();
        }
    }

    @OnClick(R.id.append_upload_image_btn)
    void onUploadImageClicked() {
        mImageUploadHelper.pickImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mImageUploadHelper != null) {
            mImageUploadHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImageUploadHelper != null) {
            mImageUploadHelper.onDestroy();
        }
    }

    private void insertImageLink(String imageLink) {
        int cursorPos = mContentET.getSelectionStart();
        String currentText = mContentET.getText().toString();
        String prefix = (cursorPos == 0 || currentText.charAt(cursorPos - 1) == '\n') ? "" : "\n";
        String suffix = "\n";
        String newText = currentText.substring(0, cursorPos) + prefix + imageLink + suffix + currentText.substring(cursorPos);
        mContentET.setText(newText);
        mContentET.setSelection(cursorPos + prefix.length() + imageLink.length() + suffix.length());
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this)
                .putExtra(KEY_TOPIC_ID, mTopicId)
                .putExtra(KEY_PAGE_INFO, mPageInfo)
                .putExtra(KEY_CONTENT, mContentET.getText().toString())
                .reload();
    }

    @Override
    public void fillView(AppendTopicPageInfo pageInfo) {
        mPageInfo = pageInfo;
        StringBuilder hint = new StringBuilder("在此输入附言内容...\n\n");
        for (int i = 0; i < mPageInfo.getTips().size(); i++) {
            hint.append(i + 1 + ". " + mPageInfo.getTips().get(i).text + "\n");
        }
        mContentET.setHint(hint.toString());
    }

    @Override
    public void handleError(GeneralError generalError) {
        String response = generalError.getResponse();
        if (Check.isEmpty(response)) return;
        Observable.just(response)
                .compose(rx(null))
                .map(s -> {
                    // First, check if this is actually a successful append
                    // (V2EX may return the topic page on success, which triggers error handler due to redirects)
                    TopicInfo topicInfo = APIService.fruit().fromHtml(s, TopicInfo.class);
                    if (isSuccessfulTopicResponse(topicInfo)) {
                        return topicInfo;
                    }
                    // If not a valid topic, try parsing as error page
                    return APIService.fruit().fromHtml(s, AppendTopicPageInfo.class);
                })
                .subscribe(new GeneralConsumer<BaseInfo>() {
                    @Override
                    public void onConsume(BaseInfo baseInfo) {
                        if (baseInfo instanceof TopicInfo) {
                            // Actually a success! Treat it as such
                            onAfterAppendTopic((TopicInfo) baseInfo);
                        } else if (baseInfo instanceof AppendTopicPageInfo) {
                            AppendTopicPageInfo pageInfo = (AppendTopicPageInfo) baseInfo;
                            AppendTopicPageInfo.Problem problem = pageInfo.getProblem();
                            if (problem != null) {
                                StringBuilder msg = new StringBuilder();
                                for (int i = 0; i < problem.getTips().size(); i++) {
                                    msg.append(i + 1).append(". ").append(problem.getTips().get(i)).append("\n");
                                }
                                new ConfirmDialog.Builder(getActivity())
                                        .title(problem.getTitle())
                                        .msg(msg.toString())
                                        .positiveText(R.string.ok)
                                        .build().show();
                            }
                        }
                    }
                });
    }

    private boolean isSuccessfulTopicResponse(TopicInfo topicInfo) {
        if (topicInfo == null || !topicInfo.isValid()) {
            return false;
        }
        TopicInfo.Problem problem = topicInfo.getProblem();
        return (problem == null || problem.isEmpty()) && Check.notEmpty(topicInfo.getTopicLink());
    }

    @Override
    public void onAfterAppendTopic(TopicInfo topicInfo) {
        Utils.toggleKeyboard(false, mContentET);
        Navigator.from(this)
                .to(TopicActivity.class)
                .addFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(TopicActivity.TOPIC_INTO_KEY, topicInfo)
                .putExtra(TopicActivity.TOPIC_ID_KEY, mTopicId)
                .start();
        // TODO: 2020-01-10 你不能为一个创建30分钟内的主题添加附言
    }

}
