package ru.yandex.whocallsya.network.pojo;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false, name = "group")
public class YandexGroup {

    @Element(required = false)
    private String relevance;
    @Element
    private YandexDoc doc;
    @Element
    private String doccount;
    @Element
    private YandexCateg categ;

    public String getRelevance() {
        return relevance;
    }

    public YandexDoc getDoc() {
        return doc;
    }

    public String getDoccount() {
        return doccount;
    }

    public YandexCateg getCateg() {
        return categ;
    }

    @Override
    public String toString() {
        return "YandexGroup[" +
//                "relevance = " + relevance +
                ", doc = " + doc.toString() +
//                ", doccount = " + doccount + ", categ = " + categ +
                "]\n";
    }

    private static class YandexCateg {

        @Attribute
        private String name;
        @Attribute
        private String attr;

        public String getName() {
            return name;
        }

        public String getAttr() {
            return attr;
        }

        @Override
        public String toString() {
            return "YandexCateg [name = " + name + ", attr = " + attr + "]";
        }
    }
}
