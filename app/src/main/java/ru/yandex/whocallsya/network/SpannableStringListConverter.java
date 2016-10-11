package ru.yandex.whocallsya.network;

import android.text.SpannableStringBuilder;
import android.util.Log;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.whocallsya.network.pojo.YandexDoc;

public class SpannableStringListConverter implements Converter<YandexDoc.Passages> {

    public YandexDoc.Passages read(InputNode node) {
        SpannableStringConverter converter = new SpannableStringConverter();
        List<SpannableStringBuilder> list = new ArrayList<>();
        InputNode child = getNextNode(node);
        while (child != null) {
            SpannableStringBuilder e = converter.read(child);
            list.add(e);
            child = getNextNode(node);
        }

        return new YandexDoc.Passages(list);
    }

    public void write(OutputNode node, YandexDoc.Passages external) {
        Log.e("whocallsyaConverter", "Auto-generated method stub: write");
    }

    private InputNode getNextNode(InputNode node) {
        InputNode next;
        try {
            next = node.getNext();
        } catch (Exception e) {
            next = null;
        }
        return next;
    }

}