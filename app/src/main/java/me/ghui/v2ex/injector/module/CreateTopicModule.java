package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.module.create.CreateTopicActivity;
import me.ghui.v2ex.module.create.CreateTopicContract;
import me.ghui.v2ex.module.create.CreateTopicPresenter;

/**
 * Created by ghui on 05/06/2017.
 */

@Module
public class CreateTopicModule {
    CreateTopicActivity mTopicActivity;

    public CreateTopicModule(CreateTopicActivity topicActivity) {
        mTopicActivity = topicActivity;
    }

    @Provides
    public CreateTopicContract.IPresenter providePresenter() {
        return new CreateTopicPresenter(mTopicActivity);
    }
}
