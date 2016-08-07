package ru.yandex.whocallsya.developer_settings;

import android.support.annotation.NonNull;

import ru.yandex.whocallsya.ui.fragment.DeveloperSettingsFragment;

import dagger.Subcomponent;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}
