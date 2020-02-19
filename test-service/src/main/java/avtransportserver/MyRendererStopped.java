package avtransportserver;

import java.net.URI;

import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import org.fourthline.cling.support.avtransport.impl.state.Stopped;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.AVTransport;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import vlcdemo.MyMediaPlayer;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 20:43
 */
public class MyRendererStopped extends Stopped {
    public MyRendererStopped(AVTransport transport) {
        super(transport);
    }

    public void onEntry() {
        super.onEntry();
        // Optional: Stop playing, release resources, etc.
        MyMediaPlayer.stopVideo();
        // If you can, you should find and set the duration of the track here!
        String metaData = getTransport().getPositionInfo().getTrackMetaData();
        String uri = getTransport().getPositionInfo().getTrackURI();
        getTransport().setPositionInfo(new PositionInfo(1, metaData, uri));
    }

    public void onExit() {
        // Optional: Cleanup etc.
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // This operation can be triggered in any state, you should think
        // about how you'd want your player to react. If we are in Stopped
        // state nothing much will happen, except that you have to set
        // the media and position info, just like in MyRendererNoMediaPresent.
        // However, if this would be the MyRendererPlaying state, would you
        // prefer stopping first?
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
        /// Same here, if you are stopped already and someone calls STOP, well...
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> play(String speed) {
        // It's easier to let this classes' onEntry() method do the work
        return MyRendererPlaying.class;
    }

    @Override
    public Class<? extends AbstractState> next() {
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> previous() {
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> seek(SeekMode unit, String target) {
        // Implement seeking with the stream in stopped state!
        return MyRendererStopped.class;
    }
}
