package puzzle.dam.luis.com.puzzle.com.dam.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import puzzle.dam.luis.com.puzzle.R;

public class SoundManager {

    public static boolean enginePaused = false;
    private int resumeMusic = -1;
    private boolean resumeMusicLoop = false;

    private boolean isMusic;
    public void setMusic(boolean isMusic){
        this.isMusic = isMusic;
    }
    private boolean isFX;
    public void setFX(boolean isFX){
        this.isFX = isFX;
    }

    // Fx Sound
    public static final byte FX_NOSOUND = -1;
    public static final byte FX_PIECE = 0;
    public static final byte FX_BUTTON = 1;
    public static int ms_iCurrentFXClip = FX_NOSOUND;

    private SoundPool soundPool;
    private static final int FX_FILE [] = {
            R.raw.fx_piece,
            R.raw.fx_button
    };

    public static final int NUM_FXS = (byte) FX_FILE.length;

    // Music
    public static final byte MUSIC_NOSOUND = -1;
    public static final byte MUSIC_MENU = 0;
    public static final byte MUSIC_PLAY = 1;
    public static final byte MUSIC_END = 2;
    public static int currentMusicClip = MUSIC_NOSOUND;

    private MediaPlayer mediaPlayer;

    private Context context;

    private static final int MUSIC_FILE [] = {
    	    R.raw.menu,
            R.raw.play,
            R.raw.end
    };

    public SoundManager(Context context) {
        this.context = context;
        isMusic = true;
        isFX = true;
    }
    
    public void loadFX() {

        try {
            soundPool = new SoundPool(NUM_FXS, AudioManager.STREAM_MUSIC, 0);
            for (int i=0; i<FX_FILE.length; i++){
                soundPool.load(context, FX_FILE[i], 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void playMusic(int musicID, boolean loop, int delay) {
        if(enginePaused) {
            resumeMusic = musicID;
            resumeMusicLoop = loop;
            return;
        }
        if (isMusic) {
            try {
                if (mediaPlayer != null) {
                    if (currentMusicClip == musicID && mediaPlayer.isPlaying()) {
                        return;
                    }
                    stopMusic();
                }

                // create, volume and finally plays a wonderful song
                mediaPlayer = MediaPlayer.create(context, MUSIC_FILE[musicID]);
                mediaPlayer.setLooping(loop);
                mediaPlayer.setVolume(1f, 1f);
                mediaPlayer.start();

                currentMusicClip = musicID;

            } catch (Exception ex) {
                currentMusicClip = MUSIC_NOSOUND;
                ex.printStackTrace();
            }
        }
    }

    public void stopMusic() {
        try {
            if(enginePaused) {
                resumeMusic = -1;
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            currentMusicClip = MUSIC_NOSOUND;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int playFX(int fxID) {
    	ms_iCurrentFXClip = fxID;
        int streamID = 0;
        if (isFX && fxID > FX_NOSOUND) {
            
            try {
                streamID = soundPool.play(fxID+1, 1f, 1f, 1, 0, 1f);

            } catch (Exception ex) {
            }
        }
        return streamID;
    }

    public void flushSndManager () {
        try {
            soundPool.release();
            soundPool = null;
            mediaPlayer = null;
        } catch (Exception ex) {
        }
    }
    
    
}