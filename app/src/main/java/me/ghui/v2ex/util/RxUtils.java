package me.ghui.v2ex.util;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ghui on 28/03/2017.
 */

public class RxUtils {
	private static ObservableTransformer IO_TRANSFORMER;

	public static <T> ObservableTransformer<T, T> io_main() {
		if (IO_TRANSFORMER == null) {
			IO_TRANSFORMER = new ObservableTransformer() {
				@Override
				public ObservableSource apply(Observable upstream) {
					return upstream.subscribeOn(Schedulers.io())
							.unsubscribeOn(Schedulers.io())
							.observeOn(AndroidSchedulers.mainThread());
				}
			};
		}
		//noinspection unchecked
		return IO_TRANSFORMER;
	}

}
