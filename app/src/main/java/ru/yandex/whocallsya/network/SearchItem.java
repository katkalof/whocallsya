package ru.yandex.whocallsya.network;

import android.text.SpannableStringBuilder;

public class SearchItem {
    private final SpannableStringBuilder title;
    private final SpannableStringBuilder link;
    private final SpannableStringBuilder description;

    public SearchItem(SpannableStringBuilder title, SpannableStringBuilder link, SpannableStringBuilder description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public SpannableStringBuilder getTitle() {
        return title;
    }

    public SpannableStringBuilder getUrl() {
        return link;
    }

    public SpannableStringBuilder getDescription() {
        return description;
    }
}