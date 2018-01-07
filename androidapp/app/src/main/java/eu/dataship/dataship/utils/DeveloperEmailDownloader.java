package eu.dataship.dataship.utils;

import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class DeveloperEmailDownloader {

    private String baseURL = "https://play.google.com/store/apps/details?id=";
    private String baseURLlanguage = "&hl=en";
    private String cssSelector = ".content.contains-text-link .dev-link";

    public String getURLfromPackageName(String packageName) {
        return baseURL + packageName + baseURLlanguage;
    }

    @Nullable
    public String getEmail(String packageName) {
        String email = null;
        Document document;
        try {
            document = Jsoup.connect(getURLfromPackageName(packageName)).get();
            email = document.select(cssSelector).toString();
            for (Element el : document.select(cssSelector)) {
                if (el.text().contains("Email")) {
                    String[] splitted = el.text().trim().split("\\s+");
                    email = splitted.toString();
                    if (splitted.length > 0) {
                        email = splitted[1];
                    }
                }
            }
        } catch (IOException e) {
            email = "exception";
            e.printStackTrace();
        }

        return email;
    }
}
