/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package org.madsonic.service.jukebox;

import org.madsonic.Logger;
import org.madsonic.service.JukeboxService;
import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.madsonic.service.jukebox.AudioPlayer.State.*;

/**
 * A simple wrapper for playing sound from an input stream.
 * <p/>
 * Supports pause and resume, but not restarting.
 *
 * @author Sindre Mehus
 * @version $Id: AudioPlayer.java 3701 2013-11-27 11:15:27Z sindre_mehus $
 */
public class AudioPlayer {

    public static final float DEFAULT_GAIN = 0.75f;
    private static final Logger LOG = Logger.getLogger(JukeboxService.class);

    private final InputStream in;
    private final Listener listener;
    private final SourceDataLine line;
    private final AtomicReference<State> state = new AtomicReference<State>(PAUSED);
    private FloatControl gainControl;

    public AudioPlayer(InputStream in, Listener listener) throws Exception {
        this.in = new BufferedInputStream(in);
        this.listener = listener;

        AudioFormat format = AudioSystem.getAudioFileFormat(this.in).getFormat();
        line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        LOG.debug("Opened line " + line);

        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            setGain(DEFAULT_GAIN);
        }
        new AudioDataWriter();
    }

    /**
     * Starts (or resumes) the player.  This only has effect if the current state is
     * {@link State#PAUSED}.
     */
    public synchronized void play() {
        if (state.get() == PAUSED) {
            line.start();
            setState(PLAYING);
        }
    }

    /**
     * Pauses the player.  This only has effect if the current state is
     * {@link State#PLAYING}.
     */
    public synchronized void pause() {
        if (state.get() == PLAYING) {
            setState(PAUSED);
            line.stop();
            line.flush();
        }
    }

    /**
     * Closes the player, releasing all resources. After this the player state is
     * {@link State#CLOSED} (unless the current state is {@link State#EOM}).
     */
    public synchronized void close() {
        if (state.get() != CLOSED && state.get() != EOM) {
            setState(CLOSED);
        }

        try {
            line.stop();
        } catch (Throwable x) {
            LOG.warn("Failed to stop player: " + x, x);
        }
        try {
            if (line.isOpen()) {
                line.close();
                LOG.debug("Closed line " + line);
            }
        } catch (Throwable x) {
            LOG.warn("Failed to close player: " + x, x);
        }
        IOUtils.closeQuietly(in);
    }

    /**
     * Returns the player state.
     */
    public State getState() {
        return state.get();
    }

    /**
     * Sets the gain.
     *
     * @param gain The gain between 0.0 and 1.0.
     */
    public void setGain(float gain) {
        if (gainControl != null) {

            double minGainDB = gainControl.getMinimum();
            double maxGainDB = Math.min(0.0, gainControl.getMaximum());  // Don't use positive gain to avoid distortion.
            double ampGainDB = 0.5f * maxGainDB - minGainDB;
            double cste = Math.log(10.0) / 20;
            double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * gain);

            valueDB = Math.min(valueDB, maxGainDB);
            valueDB = Math.max(valueDB, minGainDB);

            gainControl.setValue((float) valueDB);
        }
    }

    /**
     * Returns the position in seconds.
     */
    public int getPosition() {
        return (int) (line.getMicrosecondPosition() / 1000000L);
    }

    private void setState(State state) {
        if (this.state.getAndSet(state) != state && listener != null) {
            listener.stateChanged(this, state);
        }
    }

    private class AudioDataWriter implements Runnable {

        public AudioDataWriter() {
            new Thread(this).start();
        }

        public void run() {
            try {
                byte[] buffer = new byte[8192];

                while (true) {

                    switch (state.get()) {
                        case CLOSED:
                        case EOM:
                            return;
                        case PAUSED:
                            Thread.sleep(250);
                            break;
                        case PLAYING:
                            int n = in.read(buffer);
                            if (n == -1) {
                                setState(EOM);
                                return;
                            }
                            line.write(buffer, 0, n);
                            break;
                    }
                }
            } catch (Throwable x) {
                LOG.warn("Error when copying audio data: " + x, x);
            } finally {
                close();
            }
        }
    }

    public interface Listener {
        void stateChanged(AudioPlayer player, State state);
    }

    public static enum State {
        PAUSED,
        PLAYING,
        CLOSED,
        EOM
    }
}
