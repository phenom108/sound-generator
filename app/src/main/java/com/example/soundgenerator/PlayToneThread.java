package com.example.soundgenerator;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

public class PlayToneThread extends Thread {

    private boolean isPlaying = false;
    private final int freqOfTone;
    private final int duration;
    private AudioTrack audioTrack = null;
    private final ToneStoppedListener toneStoppedListener;
    private int volume;
    private int wave;

    public PlayToneThread(int freqOfTone, int duration, int volume, int wave,
                          ToneStoppedListener toneStoppedListener) {
        this.freqOfTone = freqOfTone;
        this.duration = duration;
        this.toneStoppedListener = toneStoppedListener;
        this.volume = volume;
        this.wave = wave;
    }

    @Override
    public void run() {
        super.run();
        playTone();
    }

    private void playTone() {
        if (!isPlaying) {
            isPlaying = true;

            int sampleRate = 44100;  // 44.1 KHz  44100
            double dnumSamples = (double) duration * sampleRate;
            dnumSamples = Math.ceil(dnumSamples);

            int numSamples = (int) dnumSamples;
            double[] sample = new double[numSamples];
            byte[] generatedSnd = new byte[2 * numSamples];

            double f = freqOfTone;
            double q = 0;
            final double K = 2.0 * Math.PI / sampleRate;

            for (int i = 0; i < numSamples; ++i) {
                if (wave == 1) {
                    sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
                } else if (wave == 2) {
                    f += (freqOfTone - f) / 4096.0;
                    q += (q < Math.PI) ? f * K : (f * K) - (2.0 * Math.PI);
                    sample[i] = (short) ((q > 0.0) ? 1 : -1);
                }
                else if (wave==3){
                    f += (freqOfTone - f) / 4096.0;
                    q += (q < Math.PI) ? f * K : (f * K) - (2.0 * Math.PI);
                    sample[i] = (short) Math.round((q / Math.PI));
                }
            }


            int idx = 0;
            int i;
            int ramp = numSamples / 20;

            for (i = 0; i < ramp; ++i) {
                final short val = (short) (sample[i] * 32767 * i / ramp);
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }

            for (i = ramp; i < numSamples - ramp; ++i) {
                final short val = (short) (sample[i] * 32767);
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }

            for (i = numSamples - ramp; i < numSamples; ++i) {
                final short val = (short) (sample[i] * 32767 * (numSamples - i) / ramp);
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }

            try {
                int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM);

                audioTrack.setNotificationMarkerPosition(numSamples);
                audioTrack.setPlaybackPositionUpdateListener(
                        new AudioTrack.OnPlaybackPositionUpdateListener() {
                            @Override
                            public void onPeriodicNotification(AudioTrack track) {
                                // nothing to do
                            }

                            @Override
                            public void onMarkerReached(AudioTrack track) {
                                toneStoppedListener.onToneSTopped();
                            }
                        });


                int maxVolume = (int) AudioTrack.getMaxVolume();

                if (volume > maxVolume) {
                    volume = maxVolume;
                } else if (volume < 0) {
                    volume = 0;
                }
                audioTrack.setVolume(volume);

                audioTrack.play();
                audioTrack.write(generatedSnd, 0, generatedSnd.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopTone();
        }
    }

    /**
     * Stop tone.
     */
    void stopTone() {
        if (audioTrack != null && audioTrack.getState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
            audioTrack.release();
            isPlaying = false;
        }
    }
}
