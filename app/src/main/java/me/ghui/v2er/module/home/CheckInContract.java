package me.ghui.v2er.module.home;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.CheckInInfo;
import me.ghui.v2er.widget.FollowProgressBtn;
import me.ghui.v2er.widget.MaterialProgressDrawable;

/**
 * Created by ghui on 07/08/2017.
 */

public class CheckInContract {
    public interface IView extends BaseContract.IView {
        //        void onGetCheckInInfo(CheckInInfo checkInInfo);
        FollowProgressBtn checkInBtn();
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void checkIn(boolean needAutoCheckIn);
    }


}
