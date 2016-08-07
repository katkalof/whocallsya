package ru.yandex.whocallsya.service;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

import ru.yandex.whocallsya.ui.view.InformingLayout;

public class SearchAsyncTask extends AsyncTask<String, Void, SearchAsyncTask.Response> {

    private final InformingLayout informingLayout;

    public SearchAsyncTask(InformingLayout informingLayout) {
        this.informingLayout = informingLayout;
    }

    @Override
    protected Response doInBackground(String... searchStrings) {
        String searchLink = searchStrings[0].replace(" ", "%20").replace("+", "%2B");
        Document doc;
        try {
            doc = Jsoup.connect(
                    new StringBuilder(searchLink)
                            .append("&lr=")
                            .append(new Random(System.currentTimeMillis()).nextInt())
                            .toString())
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .get();
        } catch (IOException e) {
            Log.e("SearchAsyncTask", "something wrong in get!");
            return new Response("Don't have internet response.", searchLink, "Tap icon and continue in your browser");
        }
        Elements result = doc.getElementsByClass("serp-item__title-link");
        if (result.isEmpty()) {
            Log.e("SearchAsyncTask", searchLink);
            Log.e("SearchAsyncTask", "Ban!");
            Log.e("SearchAsyncTask", doc.toString());
            return new Response("You was baned in yandex!", searchLink, "Tap and and continue in your browser");
        }
        String title = result.text();
        String link = result.get(0).attr("href");

        result = doc.getElementsByClass("organic__content-wrapper");
        String desc = result.get(0).text();

        return new Response(title, link, desc);
    }

    @Override
    protected void onPostExecute(Response response) {
        informingLayout.setPreview(response);
    }


    public static class Response {
        private final String title;
        private final String link;
        private final String description;

        private Response(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }
    }
}