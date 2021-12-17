package me.ghui.v2er.module.home;

public class MinePresenter implements MineContract.IPresenter {

    private MineContract.IView mView;

    public MinePresenter(MineContract.IView mView) {
        this.mView = mView;
    }

    @Override
    public void start() {

    }

}
