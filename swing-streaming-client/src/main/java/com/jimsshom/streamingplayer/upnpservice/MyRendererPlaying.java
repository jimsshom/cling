package com.jimsshom.streamingplayer.upnpservice;

import java.net.URI;

import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;
import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import org.fourthline.cling.support.avtransport.impl.state.Playing;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.AVTransport;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 20:44
 */
public class MyRendererPlaying extends Playing {

    public MyRendererPlaying(AVTransport transport) {
        super(transport);
    }

    @Override
    public void onEntry() {
        super.onEntry();
        // Start playing now!
        EventBusManager.fireEvent(EventType.START_NEW_URL, getTransport().getMediaInfo().getCurrentURI());
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // Your choice of action here, and what the next state is going to be!
        getTransport().setMediaInfo(
            new MediaInfo(uri.toString(), metaData)
        );

        // If you can, you should find and set the duration of the track here!
        getTransport().setPositionInfo(
            new PositionInfo(1, metaData, uri.toString())
        );

        // It's up to you what "last changes" you want to announce to event listeners
        getTransport().getLastChange().setEventedValue(
            getTransport().getInstanceId(),
            new AVTransportVariable.AVTransportURI(uri),
            new AVTransportVariable.CurrentTrackURI(uri)
        );

        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        // Stop playing!
        return MyRendererStopped.class;
    } // DOC:INC1

    @Override
    public Class<? extends AbstractState> play(String speed) {
        return null;
    }

    @Override
    public Class<? extends AbstractState> pause() {
        return null;
    }

    @Override
    public Class<? extends AbstractState> next() {
        return null;
    }

    @Override
    public Class<? extends AbstractState> previous() {
        return null;
    }

    @Override
    public Class<? extends AbstractState> seek(SeekMode unit, String target) {
        if (SeekMode.ABS_TIME.equals(unit) || SeekMode.REL_TIME.equals(unit)) {
            EventBusManager.fireEvent(EventType.SEEK_BY_TIME, String.valueOf(parseTimeText(target)));
        }
        return null;
    }

    private long parseTimeText(String text) {
        String[] split = text.split(":");
        if (split.length != 3) {
            return 0;
        }
        int h = Integer.valueOf(split[0]);
        int m = Integer.valueOf(split[1]);
        int s = Integer.valueOf(split[2]);
        return (h*3600 + m * 60 + s) * 1000;
    }
}
