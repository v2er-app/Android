package me.ghui.v2er.module.home;

import me.ghui.v2er.R;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.DailyInfo;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;

import static me.ghui.v2er.widget.FollowProgressBtn.FINISHED;
import static me.ghui.v2er.widget.FollowProgressBtn.NORMAL;

/**
 * Created by ghui on 07/08/2017.
 */

public class CheckInPresenter implements CheckInContract.IPresenter {
    private CheckInContract.ICheckInCallBack callBack;
    private String checkInDaysStr;

    public CheckInPresenter(CheckInContract.ICheckInCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void start() {
        checkIn(Pref.readBool(R.string.pref_key_auto_checkin));
    }

    @Override
    public void checkIn(boolean needAutoCheckIn) {
        APIService.get().dailyInfo()
                .compose(RxUtils.io_main())
                .subscribe(new GeneralConsumer<DailyInfo>(callBack) {
                    @Override
                    public void onConsume(DailyInfo checkInInfo) {
                        if (checkInInfo.hadCheckedIn()) {
                            checkInDaysStr = checkInInfo.getCheckinDays();
                            if (callBack != null) {
                                callBack.onHasChekIn(checkInDaysStr);
                            }
                        } else {
                            if (needAutoCheckIn) {
                                checkIn(checkInInfo.once());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    @Override
    public int checkInDays() {
        return Utils.getIntFromString(checkInDaysStr);
    }

    private void checkIn(String once) {
        APIService.get()
                .checkIn(once)
                .compose(RxUtils.io_main())
                .subscribe(new GeneralConsumer<DailyInfo>(callBack) {
                    @Override
                    public void onConsume(DailyInfo checkInInfo) {
                        if (checkInInfo.hadCheckedIn()) {
                            checkInDaysStr = checkInInfo.getCheckinDays();
                            if (callBack != null) {
                                callBack.onCheckInSuccess(checkInDaysStr);
                            }
                        } else {
                            if (callBack != null) {
                                callBack.onCheckInFail();
                            }
                        }
                    }
                });
    }
}
