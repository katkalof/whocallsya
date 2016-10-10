package ru.yandex.whocallsya.network.pojo;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class YandexSearch {

    @Element
    @Path("response/results")
    private YandexGrouping grouping;

    public YandexGrouping getGrouping() {
        return grouping;
    }

    @Override
    public String toString() {
        if (grouping != null) {
            return grouping.getList().toString();
        } else {
            return "YandexSearch is null";
        }
    }

    @Root(strict = false, name = "grouping")
    private static class YandexGrouping {

        @ElementList(inline = true, entry = "group")
        private List<YandexGroup> list;

        public List<YandexGroup> getList() {
            return list;
        }

    }
}
