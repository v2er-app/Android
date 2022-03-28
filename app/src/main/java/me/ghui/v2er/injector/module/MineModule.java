package me.ghui.v2er.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.MineContract;
import me.ghui.v2er.module.home.MineFragment;
import me.ghui.v2er.module.home.MinePresenter;
import me.ghui.v2er.module.home.MsgContract;
import me.ghui.v2er.module.home.MsgPresenter;
import me.ghui.v2er.network.bean.UserPageInfo;

@Module
public class MineModule {

    private MineFragment mView;

    public MineModule(MineFragment mView) {
        this.mView = mView;
    }

    @PerFragment
    @Provides
    public MineContract.IPresenter providePresenter() {
        return new MinePresenter(mView);
    }

}
