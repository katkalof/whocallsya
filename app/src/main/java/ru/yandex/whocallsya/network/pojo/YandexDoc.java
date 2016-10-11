package ru.yandex.whocallsya.network.pojo;

import android.text.SpannableStringBuilder;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import java.util.List;

import ru.yandex.whocallsya.network.SpannableStringConverter;
import ru.yandex.whocallsya.network.SpannableStringListConverter;

@Root(strict = false)
public class YandexDoc {

    @Element
    private String url;
    @Element(required = false)
    private Passages passages;

    @Element(required = false)
    @Convert(value = SpannableStringConverter.class)
    private SpannableStringBuilder title;
    @Element(required = false)
    private String headline;
    @Element
    private String domain;

    public String getUrl() {
        return url;
    }

    public Passages getPassages() {
        return passages;
    }

    public SpannableStringBuilder getTitle() {
        return title;
    }

    public String getHeadline() {
        return headline;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return "YandexDoc [" +
//                "url = " + url +
//                ", passages = " + passages +
                ", title = " + title +
//                ", headline = " + headline +
//                ", domain = " + domain +
                "]";
    }

    @Root(strict = false, name = "passages")
    @Convert(value = SpannableStringListConverter.class)
    public static class Passages {

        @ElementList(inline = true, entry = "passage")
        private List<SpannableStringBuilder> passages;

        public Passages(List<SpannableStringBuilder> spannableStringBuilders) {
            this.passages = spannableStringBuilders;
        }

        public List<SpannableStringBuilder> getPassagesList() {
            return passages;
        }

        @Override
        public String toString() {
            return "Passages [passage = " + passages + "]";
        }

    }
}
