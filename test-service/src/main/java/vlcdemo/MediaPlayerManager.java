package vlcdemo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.xml.transform.Result;

import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

/**
 * @author jimsshom
 * @date 2020/02/18
 * @time 15:10
 */
public class MediaPlayerManager {
    //private final EmbeddedMediaPlayerComponent mediaPlayerComponent; //macos下用不了，见官方文档
    private CallbackMediaPlayerComponent mediaPlayerComponent = new CallbackMediaPlayerComponent();
    private KeyAdapter exitFullScreenKeyAdapter;

    public CallbackMediaPlayerComponent getMediaPlayerComponent() {
        return mediaPlayerComponent;
    }

    public void initialEventListener(final PlayerFrame playerFrame) {
        mediaPlayerComponent.mediaPlayer().input().enableKeyInputHandling(false);
        mediaPlayerComponent.mediaPlayer().input().enableMouseInputHandling(false);

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("error");
                super.error(mediaPlayer);
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                //playerFrame.closeFrameBySwing();
                //super.finished(mediaPlayer);
                //finish和stop消息会同时触发
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
                playerFrame.updatePlayProgressBySwing(newTime);
                super.timeChanged(mediaPlayer, newTime);
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                System.out.println("playing");
                super.playing(mediaPlayer);
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                System.out.println("stopped");
                super.stopped(mediaPlayer);
            }
        });

        mediaPlayerComponent.mediaPlayer().events().addMediaEventListener(new MediaEventAdapter() {
            @Override
            public void mediaDurationChanged(Media media, final long newDuration) {
                playerFrame.updateTotalDurationBySwing(newDuration);
                super.mediaDurationChanged(media, newDuration);
            }
        });

        exitFullScreenKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 27) {
                    toggleFullScreen();
                }
                super.keyPressed(e);
            }
        };

        mediaPlayerComponent.mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(playerFrame.getFrame()) {
            @Override
            protected void onBeforeEnterFullScreen() {
                playerFrame.hideControlPane();
                mediaPlayerComponent.videoSurfaceComponent().addKeyListener(exitFullScreenKeyAdapter);
                super.onBeforeEnterFullScreen();
            }

            @Override
            protected void onAfterExitFullScreen() {
                playerFrame.showControlPane();
                mediaPlayerComponent.videoSurfaceComponent().removeKeyListener(exitFullScreenKeyAdapter);
                super.onAfterExitFullScreen();
            }
        });

        mediaPlayerComponent.videoSurfaceComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPlaying()) {
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            pause();
                        }
                    });
                    playerFrame.updateIconWhenPauseBySwing();
                } else {
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            play();
                        }
                    });
                    playerFrame.updateIconWhenPlayBySwing();
                }
                super.mouseClicked(e);
            }
        });

        mediaPlayerComponent.videoSurfaceComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 39) {//right arrow
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            skipByTime(10000);
                        }
                    });
                } else if (e.getKeyCode() == 37) { //left arrow
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            skipByTime(-10000);
                        }
                    });
                }
                super.keyPressed(e);
            }
        });
    }

    public void startPlayByUrl(String url) {
        System.out.println("play=" + url);
        boolean result = mediaPlayerComponent.mediaPlayer().media().play(url);
        mediaPlayerComponent.mediaPlayer().videoSurface().attachVideoSurface();
        System.out.println("playResult=" + result);
        returnFocus();
    }

    public void releaseResource() {
        mediaPlayerComponent.release();
    }

    public boolean isPlaying() {
        return mediaPlayerComponent.mediaPlayer().status().isPlaying();
    }

    public void play() {
        mediaPlayerComponent.mediaPlayer().controls().play();
    }

    public void pause() {
        mediaPlayerComponent.mediaPlayer().controls().pause();
    }

    public void stop() {
        mediaPlayerComponent.mediaPlayer().controls().stop();
    }

    public void toggleFullScreen() {
        mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
    }

    public void seekByTime(long time) {
        mediaPlayerComponent.mediaPlayer().controls().setTime(time);
    }

    public void skipByTime(long time) {
        mediaPlayerComponent.mediaPlayer().controls().skipTime(time);
    }

    public void setVolume(int value) {
        mediaPlayerComponent.mediaPlayer().audio().setVolume(value);
    }

    public void returnFocus() {
        mediaPlayerComponent.videoSurfaceComponent().requestFocus();
    }
}
