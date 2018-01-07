package eu.dataship.dataship;

import org.junit.Test;

import eu.dataship.dataship.utils.DeveloperEmailDownloader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DeveloperEmailDownloaderTest {

    private String packageName1 = "com.ran3000.tright";
    private String URL1 = "https://play.google.com/store/apps/details?id=com.ran3000.tright&hl=en";
    private String email1 = "giacomoran@gmail.com";
    private String packageName2 = "com.facebook.katana";
    private String URL2 = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en";
    private String email2 = "android-support@fb.com";

    @Test
    public void emailDownloader_getURL1_isCorrect() {
        DeveloperEmailDownloader developerEmailDownloader = new DeveloperEmailDownloader();
        assertThat(developerEmailDownloader.getURLfromPackageName(packageName1), is(URL1));
    }

    @Test
    public void emailDownloader_getURL2_isCorrect() {
        DeveloperEmailDownloader developerEmailDownloader = new DeveloperEmailDownloader();
        assertThat(developerEmailDownloader.getURLfromPackageName(packageName2), is(URL2));
    }

    @Test
    public void emailDownloader_getEmail1_isCorrect() {
        DeveloperEmailDownloader developerEmailDownloader = new DeveloperEmailDownloader();
        assertThat(developerEmailDownloader.getEmail(packageName1), is(email1));
    }

    @Test
    public void emailDownloader_getEmail2_isCorrect() {
        DeveloperEmailDownloader developerEmailDownloader = new DeveloperEmailDownloader();
        assertThat(developerEmailDownloader.getEmail(packageName2), is(email2));
    }
}