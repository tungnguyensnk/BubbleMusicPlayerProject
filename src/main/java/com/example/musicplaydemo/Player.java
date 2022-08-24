package com.example.musicplaydemo;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class Player extends PApplet {

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public Player(String path) {
        setPath(path);
    }

    Minim minim;
    AudioPlayer player;
    boolean pauseCheck = true;
    boolean muteCheck = true;

    @Override
    public void setup() {
        sketchPath();
        try{
            File file = new File(path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);

            AudioFormat audioFormat = ais.getFormat();
            AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    audioFormat.getSampleRate(), 16,
                    audioFormat.getChannels(),
                    audioFormat.getChannels() * 2,
                    audioFormat.getSampleRate(),
                    false);

            AudioInputStream encodedASI = AudioSystem.getAudioInputStream(convertFormat, ais);
            File filetmp = new File(System.getProperty("user.dir") + "/data/tmp.wav");
            AudioSystem.write(encodedASI, AudioFileFormat.Type.WAVE,filetmp);

        }catch(Exception e){
            e.printStackTrace();
        }
        minim = new Minim(this);
        player = minim.loadFile(System.getProperty("user.dir") + "/data/tmp.wav", 2048);
        player.play();
    }

    @Override
    public void draw() {
    }

    public void open() {
        setup();
    }

    @Override
    public void pause() {
        if (pauseCheck) {
            player.pause();
            pauseCheck = false;
        } else {
            player.play();
            pauseCheck = true;
        }
    }

    public void mute() {
        if (muteCheck) {
            player.mute();
            muteCheck = false;
        } else {
            player.unmute();
            muteCheck = true;
        }
    }

    public void close() {
        if (player != null)
            player.close();
        if (minim != null)
            minim.stop();
    }

    public boolean isRun() {
        return player.isPlaying();
    }

    public float getData() {
        return (player.mix.get(0) + player.mix.get(1) + player.mix.get(2)) / 3;
    }

    public int getLength() {
        return player.getMetaData().length();
    }

    public String getFulltime() {
        int time = getLength() / 1000;
        int gio = time / 60;
        String gioS, phutS;

        if (gio > 9)
            gioS = gio + "";
        else
            gioS = "0" + gio;

        int phut = time - gio * 60;

        if (phut > 9)
            phutS = phut + "";
        else
            phutS = "0" + phut;
        return gioS + ":" + phutS;
    }

    public String getTitle() {
        return minim.loadFile(path, 2048).getMetaData().title();
    }
}
