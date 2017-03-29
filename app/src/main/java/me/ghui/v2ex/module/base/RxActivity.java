package me.ghui.v2ex.module.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by ghui on 28/03/2017.
 */

public abstract class RxActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {
	private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();


	@NonNull
	@CheckResult
	public final Observable<ActivityEvent> lifecycle() {
		return this.lifecycleSubject.hide();
	}

	@NonNull
	@CheckResult
	public final <t> LifecycleTransformer<t> bindUntilEvent(@NonNull ActivityEvent event) {
		return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
	}

	@NonNull
	@CheckResult
	public final <t> LifecycleTransformer<t> bindToLifecycle() {
		return RxLifecycleAndroid.bindActivity(this.lifecycleSubject);
	}

	@CallSuper
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.lifecycleSubject.onNext(ActivityEvent.CREATE);
	}

	@CallSuper
	protected void onStart() {
		super.onStart();
		this.lifecycleSubject.onNext(ActivityEvent.START);
	}

	@CallSuper
	protected void onResume() {
		super.onResume();
		this.lifecycleSubject.onNext(ActivityEvent.RESUME);
	}

	@CallSuper
	protected void onPause() {
		this.lifecycleSubject.onNext(ActivityEvent.PAUSE);
		super.onPause();
	}

	@CallSuper
	protected void onStop() {
		this.lifecycleSubject.onNext(ActivityEvent.STOP);
		super.onStop();
	}

	@CallSuper
	protected void onDestroy() {
		this.lifecycleSubject.onNext(ActivityEvent.DESTROY);
		super.onDestroy();
	}
}
