package me.ghui.v2ex.module.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * Created by ghui on 30/04/2017.
 */

public interface IBindToLife {
    <K> LifecycleTransformer<K> bindToLife();
}
