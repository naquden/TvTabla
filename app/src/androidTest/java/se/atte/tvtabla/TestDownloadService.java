package se.atte.tvtabla;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.TestOnly;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDownloadService {

    private final String TEST_BODY =
//            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//            "<!DOCTYPE tv SYSTEM \"xmltv.dtd\">\n" +
//            "<tv generator-info-name=\"Taiga XMLTV\" generator-info-url=\"http://xmltv.se\">\n" +
            "    <programme start=\"20190722025100 +0200\" stop=\"20190722034800 +0200\" channel=\"hd.tv4.se\">\n" +
                    "        <title lang=\"sv\">En plats i solen: Sommarsol</title>\n" +
                    "        <sub-title lang=\"sv\">Languedoc, France</sub-title>\n" +
                    "        <desc lang=\"sv\">Richard Gansbuehler och sonen Rich Gansbuehler från England letar efter ett mysigt semesterhem. Sara hjälper dem att leta efter ett boende runt vackra Tarragona i Spanien.</desc>\n" +
                    "        <credits>\n" +
                    "            <presenter>Sara Damergi</presenter>\n" +
                    "        </credits>\n" +
                    "        <date>2012</date>\n" +
                    "        <category lang=\"en\">series</category>\n" +
                    "        <category lang=\"en\">Series</category>\n" +
                    "        <url>https://www.themoviedb.org/tv/57436/season/1/episode/8</url>\n" +
                    "        <episode-num system=\"xmltv_ns\">0 . 7/15 .</episode-num>\n" +
                    "        <episode-num system=\"onscreen\">Episode 8 of 15 season 1</episode-num>\n" +
                    "    </programme>\n"
//            "</tv>"
            ;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void parseXml() {
        String test =
                "<LIST> \n" +
                        "   <ALLFile>\n" +
                        "      <File>\n" +
                        "         <NAME>SOME FILE NAME</NAME>\n" +
                        "         <FPATH>SOME FILE PATH</FPATH>\n" +
                        "         <SIZE>160053622</SIZE>\n" +
                        "         <TIMECODE>1299673239</TIMECODE>\n" +
                        "         <TIME>2018/11/23 14:04:46</TIME>\n" +
                        "         <ATTR>33</ATTR>\n" +
                        "      </File>\n" +
                        "   </ALLFile>\n" +
                        "   <ALLFile> \n" +
                        "      <File> \n" +
                        "\t <NAME>SOME FILE NAME</NAME>\n" +
                        "         <FPATH>SOME FILE PATH</FPATH>\n" +
                        "         <SIZE>160053622</SIZE>\n" +
                        "         <TIMECODE>1299673559</TIMECODE>\n" +
                        "         <TIME>2018/11/23 14:14:46</TIME>\n" +
                        "         <ATTR>33</ATTR>\n" +
                        "      </File>\n" +
                        "   </ALLFile>\n" +
                        "</LIST>";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(test));

        mActivityRule.launchActivity(new Intent());

        int stop = 1;
    }
}
