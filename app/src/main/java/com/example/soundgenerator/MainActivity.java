package com.example.soundgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private EditText frequencyEditText;
    private EditText durationEditText;
    private EditText volumeEditText;
    private int frequency = 1000;
    private int duration = 4;
    private int volume = 1;
    private boolean isPlaying = false;
    private FloatingActionButton myFab;
    private RadioGroup rg;
    private int wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frequencyEditText = findViewById(R.id.frequency_edit_text);
        durationEditText = findViewById(R.id.duration_edit_text);
        SeekBar seekBarFreq = findViewById(R.id.seekBarFreq);
        seekBarFreq.setMax(22000);

        SeekBar seekBarDuration = findViewById(R.id.seekBarDuration);
        seekBarDuration.setMax(60);

        volumeEditText = findViewById(R.id.volume_edit_text);
        SeekBar seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarVolume.setMax(3);

        rg = findViewById(R.id.radio_group);


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.sinwave_radio) {
                    wave = 1;
                } else if (checkedId == R.id.squarewave_radio) {
                    wave = 2;
                } else if (checkedId == R.id.sawtoothwave_radio) {
                    wave = 3;
                }
            }
        });


        myFab = findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleTonePlay();
            }
        });

        seekBarFreq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequencyEditText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Stop Tone
                Tone.getInstance().stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }
        });

        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationEditText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Stop Tone
                Tone.getInstance().stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeEditText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Stop Tone
                Tone.getInstance().stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }


    private void handleTonePlay() {
        String freqString = frequencyEditText.getText().toString();
        String durationString = durationEditText.getText().toString();
        String volumeString = volumeEditText.getText().toString();

        if (!"".equals(freqString) && !"".equals(durationString)) {
            if (!isPlaying) {
                myFab.setImageResource(R.drawable.ic_stop_white_24dp);
                frequency = Integer.parseInt(freqString);
                duration = Integer.parseInt(durationString);
                volume = Integer.parseInt(volumeString);
                // Play Tone
                Tone.getInstance().generate(frequency, duration, volume, wave, new ToneStoppedListener() {
                    @Override
                    public void onToneSTopped() {
                        isPlaying = false;
                        myFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    }
                });
                isPlaying = true;
            } else {
                // Stop Tone
                Tone.getInstance().stop();
                isPlaying = false;
                myFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            }
        } else if ("".equals(freqString)) {
            Toast.makeText(MainActivity.this, "Please enter a frequency!", Toast.LENGTH_SHORT).show();
        } else if ("".equals(durationString)) {
            Toast.makeText(MainActivity.this, "Please enter duration!", Toast.LENGTH_SHORT).show();
        }
    }

}
