package ru.yandex.whocallsya.network;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class SpannableStringConverter implements Converter<SpannableStringBuilder> {

    public SpannableStringBuilder read(InputNode node) {

        SpannableStringBuilder text = new SpannableStringBuilder();
        addNodeValue(node, text);

        InputNode inputNode = getNextNode(node);
        while (inputNode != null) {
            String tag = inputNode.getName();
            if (tag.equalsIgnoreCase("hlword")) {
                int start = text.length();
                addNodeValue(inputNode, text);
                int end = text.length();
                text.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            addNodeValue(node, text);
            inputNode = getNextNode(node);
        }
        return text;
    }

    public void write(OutputNode node, SpannableStringBuilder external) {
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

    private void addNodeValue(InputNode node, SpannableStringBuilder sb) {
        try {
            String s = node.getValue();
            if (s != null) {
                sb.append(s);
            }
        } catch (Exception e) {
        }
    }

}