package me.ghui.v2er.module.home;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.GeneralError;
import me.ghui.v2er.network.IGeneralErrorHandler;
import me.ghui.v2er.network.bean.IBase;
import me.ghui.v2er.widget.FollowProgressBtn;

/**
 * Created by ghui on 07/08/2017.
 */

public class CheckInContract {

    public abstract static class ICheckInCallBack implements IGeneralErrorHandler {

        void onHasChekIn(String checkInDays) {}

        void onCheckInSuccess(String checkInDays) {}

        void onCheckInFail() {}

        @Override
        public void handleError(GeneralError generalError) {

        }

    }

    public interface IPresenter extends BaseContract.IPresenter {

        void checkIn();

        void checkInToDay();

        int checkInDays();

    }


}
