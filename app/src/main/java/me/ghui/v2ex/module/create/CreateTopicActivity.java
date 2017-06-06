package me.ghui.v2ex.module.create;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2ex.R;
import me.ghui.v2ex.injector.component.DaggerCreateTopicComponnet;
import me.ghui.v2ex.injector.module.CreateTopicModule;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.network.bean.CreateTopicPageInfo;
import me.ghui.v2ex.network.bean.TopicCreateResultInfo;
import me.ghui.v2ex.util.Utils;

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
    private CreateTopicPageInfo mTopicPageInfo;
    private CreateTopicPageInfo.BaseNode mSelectNode;

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
    }

    @Override
    protected void autoLoad() {
        mPresenter.start();
    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        super.configToolBar(toolBar);
        toolBar.inflateMenu(R.menu.post_topic_menu);//设置右上角的填充菜单
        toolBar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_create_topic:
                String title = mTitleEt.getText().toString();
                if (Utils.isEmpty(title)) {
                    mTitleTextInputLayout.setError("请输入标题");
                    return false;
                }
                if (mSelectNode == null || Utils.isEmpty(mSelectNode.getId())) {
                    Toast.makeText(this, "请选择一个节点", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String content = mContentEt.getText().toString();
                mPresenter.sendPost(title, content, mSelectNode.getId());
                return true;
        }
        return false;
    }

    @OnClick(R.id.create_topic_node_wrapper)
    void onNodeWrapperClicked(View view) {
        if (mTopicPageInfo == null) {
            return;
        }
        view.setClickable(false);
        NodeSelectFragment.newInstance(mTopicPageInfo).show(getFragmentManager(), null);
        view.setClickable(true);
    }

    @Override
    public void fillView(CreateTopicPageInfo topicPageInfo) {
        mTopicPageInfo = topicPageInfo;
        // TODO: 05/06/2017  
    }

    @Override
    public void onPostFinished(TopicCreateResultInfo resultInfo) {
        // TODO: 05/06/2017
        Utils.toast("创建成功");
    }

    @Override
    public void onSelected(CreateTopicPageInfo.BaseNode node) {
        mSelectNode = node;
        mNodeTv.setText(node.getTitle());
    }
}
