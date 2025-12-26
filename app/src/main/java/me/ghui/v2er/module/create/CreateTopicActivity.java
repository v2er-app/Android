package me.ghui.v2er.module.create;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.module.imgur.ImageUploadHelper;
import io.reactivex.Observable;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.injector.component.DaggerCreateTopicComponnet;
import me.ghui.v2er.injector.module.CreateTopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.GeneralError;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.NewUserBannedCreateInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Created by ghui on 04/06/2017.
 */

public class CreateTopicActivity extends BaseActivity<CreateTopicContract.IPresenter> implements CreateTopicContract.IView,
        Toolbar.OnMenuItemClickListener, NodeSelectFragment.OnSelectedListener {
    @BindView(R.id.create_topic_title_layout)
    TextInputLayout mTitleTextInputLayout;
    @BindView(R.id.create_topic_title_et)
    EditText mTitleEt;
    @BindView(R.id.create_topic_content_et)
    EditText mContentEt;
    @BindView(R.id.create_topic_node_wrapper)
    View mNodeWrappter;
    @BindView(R.id.create_topic_node_tv)
    TextView mNodeTv;
    @BindView(R.id.btn_upload_image)
    ImageButton mUploadImageBtn;
    private CreateTopicPageInfo mTopicPageInfo;
    private NodesInfo mNodesInfo;
    private NodesInfo.Node mSelectNode;
    private ImageUploadHelper mImageUploadHelper;

    private static final String KEY_TITLE = KEY("topic_title");
    private static final String KEY_CONTENT = KEY("topic_content");
    private static final String KEY_TOPIC_SELECT_NODE = KEY("topic_select_node");
    private static final String KEY_CREATE_TOPIC_INFO = KEY("create_topic_info");
    private static final String KEY_NODES_INFO = KEY("key_nodes_info");

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_new_topic;
    }

    @Override
    protected void startInject() {
        DaggerCreateTopicComponnet.builder().appComponent(getAppComponent())
                .createTopicModule(new CreateTopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        mTitleEt.setText(intent.getStringExtra(KEY_TITLE));
        mContentEt.setText(intent.getStringExtra(KEY_CONTENT));
        mSelectNode = (NodesInfo.Node) intent.getSerializableExtra(KEY_TOPIC_SELECT_NODE);
        if (mSelectNode != null) {
            mNodeTv.setText(mSelectNode.text);
        }
        mTopicPageInfo = (CreateTopicPageInfo) intent.getSerializableExtra(KEY_CREATE_TOPIC_INFO);
        mNodesInfo = (NodesInfo) intent.getSerializableExtra(KEY_NODES_INFO);
        mPresenter.restoreData(mTopicPageInfo, mNodesInfo);

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

    @OnClick(R.id.btn_upload_image)
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
        int cursorPos = mContentEt.getSelectionStart();
        String currentText = mContentEt.getText().toString();
        String prefix = (cursorPos == 0 || currentText.charAt(cursorPos - 1) == '\n') ? "" : "\n";
        String suffix = "\n";
        String newText = currentText.substring(0, cursorPos) + prefix + imageLink + suffix + currentText.substring(cursorPos);
        mContentEt.setText(newText);
        mContentEt.setSelection(cursorPos + prefix.length() + imageLink.length() + suffix.length());
    }

    @Override
    protected void reloadMode(int mode) {
        ActivityReloader.target(this)
                .putExtra(KEY_CREATE_TOPIC_INFO, mTopicPageInfo)
                .putExtra(KEY_NODES_INFO, mNodesInfo)
                .putExtra(KEY_TITLE, mTitleEt.getText().toString())
                .putExtra(KEY_CONTENT, mContentEt.getText().toString())
                .putExtra(KEY_TOPIC_SELECT_NODE, mSelectNode)
                .reload();
    }


    @Override
    protected void autoLoad() {
        if (mTopicPageInfo == null) {
            mPresenter.start();
        }
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        toolBar.inflateMenu(R.menu.post_topic_menu);//设置右上角的填充菜单
        toolBar.setOnMenuItemClickListener(this);
        Utils.setPaddingForStatusBar(toolBar);
        Utils.setPaddingForNavbar(mRootView);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_create_topic:
                String title = mTitleEt.getText().toString();
                if (Check.isEmpty(title)) {
                    mTitleTextInputLayout.setError("请输入标题");
                    return false;
                }
                if (mSelectNode == null || Check.isEmpty(mSelectNode.id)) {
                    Toast.makeText(this, "请选择一个节点", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String content = mContentEt.getText().toString();
                mPresenter.sendPost(title, content, mSelectNode.id);
                return true;
        }
        return false;
    }

    @OnClick(R.id.create_topic_node_wrapper)
    void onNodeWrapperClicked(View view) {
        if (mTopicPageInfo == null) {
            toast("页面参数加载中，请稍后...");
            return;
        }
        view.setClickable(false);
        // TODO: 2019-12-28  NodesInfo
        NodeSelectFragment.newInstance(mPresenter.getNodes()).show(getFragmentManager(), null);
        view.setClickable(true);
    }

    @Override
    public void fillView(CreateTopicPageInfo topicPageInfo) {
        mTopicPageInfo = topicPageInfo;
    }


    @Override
    public void onPostSuccess(TopicInfo topicInfo) {
        toast("创建成功");
        TopicActivity.open(topicInfo.getTopicLink(), this);
        finish();
    }

    @Override
    public void onPostFailure(CreateTopicPageInfo createTopicPageInfo) {
        fillView(createTopicPageInfo);
        CreateTopicPageInfo.Problem problem = createTopicPageInfo.getProblem();
        String msg = "";
        if (problem == null) {
            msg = "发贴失败";
        } else {
            for (String tip : problem.getTips()) {
                msg = msg + tip + "\n";
            }
        }
        new ConfirmDialog.Builder(getActivity())
                .title(problem.getTitle())
                .msg(msg)
                .positiveText(R.string.ok, null)
                .build().show();
    }

    @Override
    public void onSelected(NodesInfo.Node node) {
        mSelectNode = node;
        mNodeTv.setText(node.text);
    }

    @Override
    public void onBackPressed() {
        if (mTitleEt.getText().length() > 0 || mContentEt.getText().length() > 0) {
            new ConfirmDialog.Builder(this)
                    .title("丢弃主题")
                    .msg("返回将丢弃当前编写的内容")
                    .positiveText(R.string.ok, dialog -> finish())
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void handleError(GeneralError generalError) {
//        super.handleError(generalError);
        String response = generalError.getResponse();
        if (Check.notEmpty(response)) {
            Observable.just(response)
                    .compose(rx(null))
                    .map(s -> {
                        // First, check if this is actually a successful topic creation
                        // (V2EX may return the topic page on success, which triggers error handler due to redirects)
                        TopicInfo topicInfo = APIService.fruit().fromHtml(s, TopicInfo.class);
                        if (isSuccessfulTopicResponse(topicInfo)) {
                            return topicInfo;
                        }
                        // If not a valid topic, try parsing as error pages
                        BaseInfo resultInfo = APIService.fruit().fromHtml(s, CreateTopicPageInfo.class);
                        if (!resultInfo.isValid()) {
                            resultInfo = APIService.fruit().fromHtml(s, NewUserBannedCreateInfo.class);
                        }
                        return resultInfo;
                    })
                    .subscribe(new GeneralConsumer<BaseInfo>(this) {
                        @Override
                        public void onConsume(BaseInfo baseInfo) {
                            if (baseInfo instanceof TopicInfo) {
                                // Actually a success! Treat it as such
                                onPostSuccess((TopicInfo) baseInfo);
                            } else if (baseInfo instanceof CreateTopicPageInfo) {
                                onPostFailure((CreateTopicPageInfo) baseInfo);
                            } else {
                                onBannedCreateTopic((NewUserBannedCreateInfo) baseInfo);
                            }
                        }
                    });
        }
    }

    private void onBannedCreateTopic(NewUserBannedCreateInfo bannedCreateInfo) {
        new ConfirmDialog.Builder(this)
                .title(bannedCreateInfo.getTitle())
                .msg(bannedCreateInfo.getErrorInfo())
                .cancelable(false)
                .negativeText(R.string.cancel, dialog -> CreateTopicActivity.this.finish())
                .positiveText("去了解", dialog -> {
                    Utils.openWap(getString(R.string.official_v2ex_about_website), getActivity());
                    CreateTopicActivity.this.finish();
                }).build().show();
    }

    private boolean isSuccessfulTopicResponse(TopicInfo topicInfo) {
        if (topicInfo == null || !topicInfo.isValid()) {
            return false;
        }
        TopicInfo.Problem problem = topicInfo.getProblem();
        return (problem == null || problem.isEmpty()) && Check.notEmpty(topicInfo.getTopicLink());
    }

}
