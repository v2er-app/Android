package me.ghui.v2ex.network;

import io.reactivex.Observable;
import me.ghui.v2ex.network.bean.NewsInfo;

/**
 * Created by ghui on 04/04/2017.
 */

public interface IJsoupService {

//	@Select("table")
	Observable<NewsInfo> homeNews();

}
