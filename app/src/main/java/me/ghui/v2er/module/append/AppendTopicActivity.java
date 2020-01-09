package me.ghui.v2er.module.append;

import android.widget.EditText;

import butterknife.BindView;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.injector.component.DaggerAppendTopicComponnet;
import me.ghui.v2er.injector.module.AppendTopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;

public class AppendTopicActivity extends BaseActivity<AppendTopicContract.IPresenter> implements AppendTopicContract.IView {

    @BindView(R.id.append_topic_content_et)
    EditText mContentET;

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
                return;
            }
            mPresenter.sendAppend(content, );
            return true;
        });
    }

    @Override
    protected void reloadMode(int mode) {

    }

    @Override
    public void fillView() {

    }

    @Override
    public void onPostSuccess(TopicInfo topicInfo) {


    }

    @Override
    public void onPostFailure() {

    }

}
