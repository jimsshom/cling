package vlcdemo;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.*;

import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

/**
 * @author xiaohe.yz
 * @date 2020/02/18
 * @time 15:10
 */
public class MediaPlayerManager {
    //private final EmbeddedMediaPlayerComponent mediaPlayerComponent; //macos下用不了，见官方文档
    private static CallbackMediaPlayerComponent mediaPlayerComponent = new CallbackMediaPlayerComponent();

    public CallbackMediaPlayerComponent getMediaPlayerComponent() {
        return mediaPlayerComponent;
    }

    public void initialEventListener(final PlayerFrame playerFrame) {
        mediaPlayerComponent.mediaPlayer().input().enableKeyInputHandling(false);
        mediaPlayerComponent.mediaPlayer().input().enableMouseInputHandling(false);

        mediaPlayerComponent.videoSurfaceComponent().requestFocus();

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        playerFrame.closeFrame();
                    }
                });
                //super.finished(mediaPlayer);
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        playerFrame.updatePlayProgress(newTime);
                    }
                });
                super.timeChanged(mediaPlayer, newTime);
            }
        });

        mediaPlayerComponent.mediaPlayer().events().addMediaEventListener(new MediaEventAdapter() {
            @Override
            public void mediaDurationChanged(Media media, final long newDuration) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        playerFrame.updateTotalDuration(newDuration);
                    }
                });
                super.mediaDurationChanged(media, newDuration);
            }
        });

        mediaPlayerComponent.mediaPlayer().fullScreen().strategy(new AdaptiveFullScreenStrategy(playerFrame.getFrame()) {
            @Override
            protected void onBeforeEnterFullScreen() {
                //controlsPane.setVisible(false);
                super.onBeforeEnterFullScreen();
            }

            @Override
            protected void onAfterExitFullScreen() {
                //controlsPane.setVisible(true);
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
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            playerFrame.updateIconWhenPause();
                        }
                    });
                } else {
                    mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                        @Override
                        public void run() {
                            play();
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            playerFrame.updateIconWhenPlay();
                        }
                    });
                }
                super.mouseClicked(e);
            }
        });

        mediaPlayerComponent.videoSurfaceComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {//FIXME: 不知为何收不到键盘消息
                System.out.println(e.getKeyCode());
                System.out.println(e.getKeyChar());
                super.keyPressed(e);
            }
        });
    }

    public void startPlayByUrl(String url) {
        mediaPlayerComponent.mediaPlayer().media().play(url);
    }

    public void releaseResource() {
        mediaPlayerComponent.release();
    }

    public boolean isPlaying() {
        return mediaPlayerComponent.mediaPlayer().media().info().state().equals(State.PLAYING);
    }

    public void pause() {
        mediaPlayerComponent.mediaPlayer().controls().pause();
    }

    public void play() {
        mediaPlayerComponent.mediaPlayer().controls().play();
    }

    public void toggleFullScreen() {
        mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
    }

    public void seekByTime(long time) {
        mediaPlayerComponent.mediaPlayer().controls().setTime(time);
    }

    public void setVolume(int value) {
        mediaPlayerComponent.mediaPlayer().audio().setVolume(value);
    }
}
