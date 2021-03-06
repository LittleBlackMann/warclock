package com.cityfruit.warclock.play;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.scene.media.MediaPlayer;
import org.springframework.util.ResourceUtils;
import sun.audio.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author TongTong
 * @date 2019/12/24
 */
public class AudioPlayRedio {

    private static AudioClip liveWarningClip;
    private static AudioClip liveOkClip;


    static {
        try {
            System.out.println("加载静态块代码");
            File liveWarningFile = new File("/Users/cf/Docker/warclock/target/classes/static/video/warning.wav");
            File liveOkFile = new File("/Users/cf/Docker/warclock/target/classes/static/video/liveok.wav");
            liveWarningClip = Applet.newAudioClip(liveWarningFile.toURL());
            liveOkClip = Applet.newAudioClip(liveOkFile.toURL());
            System.out.println("加载的文件" + liveOkFile.toString() + liveWarningFile.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void startPlay() {
        liveWarningClip.loop();

    }

    public static void stopPlay() {
        liveWarningClip.stop();
    }

    public static void okPlay() {
        liveOkClip.play();
    }

}
