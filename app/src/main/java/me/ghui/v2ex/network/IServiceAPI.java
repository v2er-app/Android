package me.ghui.v2ex.network;


import io.reactivex.Observable;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.network.bean.ResponseWrapper;
import retrofit2.http.GET;

/**
 * Created by ghui on 25/03/2017.
 */

public interface IServiceAPI {

	@GET("api/topics/hot.json")
	Observable<ResponseWrapper<DailyHotInfo>> dailyHot();

}
