package com.example.soundgenerator;

import android.os.Handler;

public class Tone {
    private PlayToneThread playToneThread;
    private boolean isThreadRunning = false;
    private final Handler stopThread;

    private static final Tone INSTANCE = new Tone();

    private Tone() {
        stopThread = new Handler();
    }

    public static Tone getInstance() {
        return INSTANCE;
    }


    public void generate(int freq, int duration, int volume,
                         ToneStoppedListener toneStoppedListener) {
        if (!isThreadRunning) {
            stop();
            playToneThread = new PlayToneThread(freq, duration, volume, toneStoppedListener);
            playToneThread.start();
            isThreadRunning = true;
            stopThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            }, duration * 1000);
        }
    }


    public void stop() {
        if (playToneThread != null) {
            playToneThread.stopTone();
            playToneThread.interrupt();
            playToneThread = null;
            isThreadRunning = false;
        }
    }
}
