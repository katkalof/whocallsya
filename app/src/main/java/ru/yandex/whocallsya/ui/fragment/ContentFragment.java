package ru.yandex.whocallsya.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.yandex.whocallsya.R;

public class ContentFragment extends BaseFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

//    @OnClick(R.id.hello)
//    void addBubbleAgain() {
//        Intent i = new Intent(getContext(), CockyBubblesService.class);
//        i.putExtra(PHONE_NUMBER, "+79992114504");
//        getActivity().startService(i);
//    }
}
