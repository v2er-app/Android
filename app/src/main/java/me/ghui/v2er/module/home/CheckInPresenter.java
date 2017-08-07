package me.ghui.v2er.module.home;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.CheckInInfo;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;

import static android.view.View.resolveSize;
import static me.ghui.v2er.widget.FollowProgressBtn.FINISHED;
import static me.ghui.v2er.widget.FollowProgressBtn.NORMAL;

/**
 * Created by ghui on 07/08/2017.
 */

public class CheckInPresenter implements CheckInContract.IPresenter {
    private CheckInContract.IView mView;
    private String checkInDaysStr;

    public CheckInPresenter(CheckInContract.IView view) {
        mView = view;
    }

    @Override
    public void start() {
        checkIn(Pref.readBool(R.string.pref_key_auto_checkin));
    }

    @Override
    public void checkIn(boolean needAutoCheckIn) {
        if (!UserUtils.isLogin()) return;
        mView.checkInBtn().startUpdate();
        APIService.get().checkInInfo()
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<CheckInInfo>() {
                    @Override
                    public void onConsume(CheckInInfo checkInInfo) {
                        if (checkInInfo.hadCheckedIn()) {
                            mView.checkInBtn().setStatus(FINISHED, "已签到", R.drawable.progress_button_done_icon);
                            checkInDaysStr = checkInInfo.getCheckinDays();
                        } else {
                            if (needAutoCheckIn) {
                                checkIn(checkInInfo.once());
                            } else {
                                mView.checkInBtn().setStatus(NORMAL, "签到", R.drawable.progress_button_checkin_icon);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.checkInBtn().setStatus(NORMAL, "签到", R.drawable.progress_button_checkin_icon);
                    }
                });
    }

    @Override
    public int checkInDays() {
        return Utils.getIntFromString(checkInDaysStr);
    }


    private void checkIn(String once) {
        mView.checkInBtn().startUpdate();
        APIService.get()
                .checkIn(once)
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<CheckInInfo>(mView) {
                    @Override
                    public void onConsume(CheckInInfo checkInInfo) {
                        if (checkInInfo.hadCheckedIn()) {
                            mView.toast("签到成功!\n" + checkInInfo.getCheckinDays());
                            mView.checkInBtn().setStatus(FINISHED, "已签到", R.drawable.progress_button_done_icon);
                            checkInDaysStr = checkInInfo.getCheckinDays();
                        } else {
                            mView.toast("签到遇到问题!");
                            mView.checkInBtn().setStatus(NORMAL, "签到", R.drawable.progress_button_checkin_icon);
                        }
                    }
                });
    }
}
