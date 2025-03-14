package com.jimsshom.streamingplayer.mediaplayer;

import java.util.ArrayList;
import java.util.List;

import com.jimsshom.streamingplayer.eventbus.EventAdapter;
import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;
import uk.co.caprica.vlcj.factory.discovery.provider.DirectoryProviderDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:06
 */
public class MediaPlayManager {
    private CallbackMediaPlayerComponent mediaPlayerComponent;

    public MediaPlayManager() {
        List<DirectoryProviderDiscoveryStrategy> defaultStrategyList = new ArrayList<>();
        defaultStrategyList.add(new LinuxNativeDiscoveryStrategy());
        defaultStrategyList.add(new OsxNativeDiscoveryStrategy());
        defaultStrategyList.add(new WindowsNativeDiscoveryStrategy());

        System.out.println("在以下目录寻找vlclib: ");
        for (DirectoryProviderDiscoveryStrategy strategy : defaultStrategyList) {
            if (strategy.supported()) {
                for (String directory : strategy.discoveryDirectories()) {
                    System.out.println(directory);
                }
            }
        }
        mediaPlayerComponent = new CallbackMediaPlayerComponent();
    }

    public void initial() {
        registerEventConsumer();
        registerEventProducer();
    }

    private void registerEventProducer() {
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void error(MediaPlayer mediaPlayer) {
                MediaPlayerLog.log("play error!!!");
                super.error(mediaPlayer);
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                MediaPlayerLog.log("finished");
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        EventBusManager.fireEvent(EventType.PROGRESS_TIME, String.valueOf(newTime));
                    }
                });
                super.timeChanged(mediaPlayer, newTime);
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        EventBusManager.fireEvent(EventType.STATUS_CHANGE, "paused");
                    }
                });
                super.paused(mediaPlayer);
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        EventBusManager.fireEvent(EventType.STATUS_CHANGE, "playing");
                    }
                });

                super.playing(mediaPlayer);
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        EventBusManager.fireEvent(EventType.STATUS_CHANGE, "stopped");
                    }
                });
                super.stopped(mediaPlayer);
            }
        });

        mediaPlayerComponent.mediaPlayer().events().addMediaEventListener(new MediaEventAdapter() {
            @Override
            public void mediaDurationChanged(Media media, final long newDuration) {
                mediaPlayerComponent.mediaPlayer().submit(new Runnable() {
                    @Override
                    public void run() {
                        EventBusManager.fireEvent(EventType.TOTAL_TIME, String.valueOf(newDuration));
                    }
                });

                super.mediaDurationChanged(media, newDuration);
            }
        });
    }

    private void registerEventConsumer() {
        EventBusManager.addEventAdapter(EventType.BEFORE_SYSTEM_EXIT, new EventAdapter() {
            @Override
            public void process(String param) {
                MediaPlayerLog.log("mediaPlayerComponent.release");
                mediaPlayerComponent.release();
            }
        });
        EventBusManager.addEventAdapter(EventType.START_NEW_URL, new EventAdapter() {
            @Override
            public void process(String param) {
                if (mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
                    mediaPlayerComponent.mediaPlayer().controls().stop();
                }
                boolean result = mediaPlayerComponent.mediaPlayer().media().play(param);
                mediaPlayerComponent.mediaPlayer().videoSurface().attachVideoSurface();
                MediaPlayerLog.log("playResult=" + result);
            }
        });
        EventBusManager.addEventAdapter(EventType.STOP, new EventAdapter() {
            @Override
            public void process(String param) {
                mediaPlayerComponent.mediaPlayer().controls().stop();
            }
        });
        EventBusManager.addEventAdapter(EventType.SEEK_BY_TIME, new EventAdapter() {
            @Override
            public void process(String param) {
                if (mediaPlayerComponent.mediaPlayer().status().isSeekable()) {
                    mediaPlayerComponent.mediaPlayer().controls().setTime(Long.parseLong(param));
                }
            }
        });
        EventBusManager.addEventAdapter(EventType.SKIP_BY_TIME, new EventAdapter() {
            @Override
            public void process(String param) {
                if (mediaPlayerComponent.mediaPlayer().status().isSeekable()) {
                    mediaPlayerComponent.mediaPlayer().controls().skipTime(Long.parseLong(param));
                }
            }
        });
        EventBusManager.addEventAdapter(EventType.PLAY_OR_PAUSE, new EventAdapter() {
            @Override
            public void process(String param) {
                if (mediaPlayerComponent.mediaPlayer().status().canPause()) {
                    mediaPlayerComponent.mediaPlayer().controls().pause();
                }
            }
        });
        EventBusManager.addEventAdapter(EventType.SET_VOLUME, new EventAdapter() {
            @Override
            public void process(String param) {
                mediaPlayerComponent.mediaPlayer().audio().setVolume(Integer.parseInt(param));
            }
        });
    }

    public CallbackMediaPlayerComponent getDisplayComponent() {
        return mediaPlayerComponent;
    }
}
