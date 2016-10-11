package ru.yandex.whocallsya;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.yandex.metrica.YandexMetrica;

import io.fabric.sdk.android.Fabric;
import ru.yandex.whocallsya.developer_settings.DevMetricsProxy;
import ru.yandex.whocallsya.developer_settings.DeveloperSettingsModel;
import timber.log.Timber;

public class App extends Application {
    private ApplicationComponent applicationComponent;

    // Prevent need in a singleton (global) reference to the application object.
    @NonNull
    public static App get(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        // Инициализация AppMetrica SDK
        YandexMetrica.activate(getApplicationContext(),"2a9d3554-92b0-450d-b761-4ad784d48468");
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);
        applicationComponent = prepareApplicationComponent().build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            DeveloperSettingsModel developerSettingModel = applicationComponent.developerSettingModel();
            developerSettingModel.apply();

            DevMetricsProxy devMetricsProxy = applicationComponent.devMetricsProxy();
            devMetricsProxy.apply();
        }
    }

    @NonNull
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this));
    }

    @NonNull
    public ApplicationComponent applicationComponent() {
        return applicationComponent;
    }
}
