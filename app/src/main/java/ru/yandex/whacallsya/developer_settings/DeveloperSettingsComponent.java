package ru.yandex.whacallsya.developer_settings;

import android.support.annotation.NonNull;

import ru.yandex.whacallsya.ui.fragments.DeveloperSettingsFragment;

import dagger.Subcomponent;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}
