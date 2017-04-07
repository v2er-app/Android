package me.ghui.v2ex.network;


import io.reactivex.Observable;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.network.converter.Json;
import retrofit2.http.GET;

/**
 * Created by ghui on 25/03/2017.
 */

public interface IServiceAPI {

	@Json
	@GET("api/topics/hot.json")
	Observable<DailyHotInfo> dailyHot();

}
