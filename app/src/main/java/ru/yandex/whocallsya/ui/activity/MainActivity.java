package ru.yandex.whocallsya.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.yandex.whocallsya.App;
import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.developer_settings.DeveloperSettingsModule;
import ru.yandex.whocallsya.ui.fragment.ContentFragment;
import ru.yandex.whocallsya.ui.other.ViewModifier;

public class MainActivity extends BaseActivity {

    @Inject
    @Named(DeveloperSettingsModule.MAIN_ACTIVITY_VIEW_MODIFIER)
    ViewModifier viewModifier;

    @SuppressLint("InflateParams") // It's okay in our case.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(this).applicationComponent().inject(this);

        setContentView(viewModifier.modify(getLayoutInflater().inflate(R.layout.activity_main, null)));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, new ContentFragment())
                    .commit();
        }
    }
}
