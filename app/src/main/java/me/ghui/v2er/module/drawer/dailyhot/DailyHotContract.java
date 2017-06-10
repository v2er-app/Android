package me.ghui.v2er.module.drawer.dailyhot;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.DailyHotInfo;

/**
 * Created by ghui on 27/03/2017.
 */

public class DailyHotContract {

	public interface IView extends BaseContract.IView {

		void fillView(DailyHotInfo dailyHotInfo);
	}

	public interface IPresenter extends BaseContract.IPresenter {

	}

}
