package ru.yandex.whocallsya.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.service.CockyBubblesService;

import static ru.yandex.whocallsya.service.CockyBubblesService.PHONE_NUMBER;

public class ContentFragment extends BaseFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @OnClick(R.id.hello)
    void addBubbleAgain() {
        Intent i = new Intent(getContext(), CockyBubblesService.class);
        i.putExtra(PHONE_NUMBER, "+74957397000");
        getActivity().startService(i);
    }
}
