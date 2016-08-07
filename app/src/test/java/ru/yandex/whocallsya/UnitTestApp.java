package ru.yandex.whocallsya;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.yandex.whocallsya.developer_settings.DevMetricsProxy;
import ru.yandex.whocallsya.developer_settings.DeveloperSettingsModule;

public class UnitTestApp extends App {

    @NonNull
    @Override
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return super.prepareApplicationComponent()
                .developerSettingsModule(new DeveloperSettingsModule() {
                    @NonNull
                    @Override
                    public DevMetricsProxy provideDevMetricsProxy(@NonNull Application application) {
                        return () -> {
                            //No Op
                        };
                    }
                });
    }
}
