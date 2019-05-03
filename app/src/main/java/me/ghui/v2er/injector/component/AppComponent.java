package me.ghui.v2er.injector.component;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import me.ghui.v2er.injector.module.AppModule;

/**
 * Created by ghui on 25/03/2017.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    Context getContext();
}
