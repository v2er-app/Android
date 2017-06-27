package me.ghui.v2er.module.base;

/**
 * Created by ghui on 03/06/2017.
 */

/**
 * Something which can handle a backpress event
 */
public interface IBackHandler {
    void handleBackable(IBackable backable);

    IBackable popBackable(IBackable backable);
}
