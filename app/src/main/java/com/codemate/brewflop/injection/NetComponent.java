package com.codemate.brewflop.injection;

import com.codemate.brewflop.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by iiro on 12.10.2016.
 */
@Singleton
@Component(modules = NetModule.class)
public interface NetComponent {
    void inject(MainActivity mainActivity);
}
