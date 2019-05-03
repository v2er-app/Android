package me.ghui.v2er.module.user;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.UserPageInfo;

/**
 * Created by ghui on 01/06/2017.
 */

public class UserHomeContract {
    public interface IView extends BaseContract.IView {
        String getUsername();

        void fillView(UserPageInfo userPageInfo);

        void afterBlockUser(boolean hadBlocked);

        void afterfollowUser(UserPageInfo userPageInfo);

    }

    public interface IPresenter extends BaseContract.IPresenter {
        void blockUser(String url);

        void followUser(String userName, String url);
    }
}
