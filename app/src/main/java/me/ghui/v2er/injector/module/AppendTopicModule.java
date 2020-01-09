package me.ghui.v2er.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.module.append.AppendTopicActivity;
import me.ghui.v2er.module.append.AppendTopicContract;
import me.ghui.v2er.module.append.AppendTopicPresenter;

@Module
public class AppendTopicModule {
    AppendTopicActivity mAppendTopicActivity;

    public AppendTopicModule(AppendTopicActivity appendTopicActivity) {
        mAppendTopicActivity = appendTopicActivity;
    }

    @Provides
    public AppendTopicContract.IPresenter providePresenter() {
        return new AppendTopicPresenter(mAppendTopicActivity);
    }

}
