package avtransportserver;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 20:30
 */

/**
 * Bilibili客户端请求信息
 *
 * setAVTransportURI
 * 0
 * http://upos-sz-mirrorcos.bilivideo.com/upgcxcode/99/21/151372199/151372199-1-208
 * .mp4?e
 * =ig8euxZM2rNcNbKV7WdVhwdl7wdBhwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_&ua=tvproj&uipk=5&nbs=1&deadline=1581607623&gen=playurl&os=cosbv&oi=3078643169&trid=c4208e172876405f8cbe1208d40baaf6T&upsig=1574ec6c8ba351a3439ab035776048a4&uparams=e,ua,uipk,nbs,deadline,gen,os,oi,trid&mid=27584
 * <DIDL-Lite  X-LeLink-Session-ID="C52AC58182FAE06BE515AD0CA185C827"
 * Content-URLID="2102C29814C28A6DF442D3F7FFF33D3F" xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/"
 * xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/" xmlns:dc="http://purl.org/dc/elements/1.1/" ><item
 * id="image-item-42" parentID="1" restricted="0"><dc:title>多屏互动投屏视频</dc:title><dc:creator>unkown</dc:creator><upnp
 * :class>object.item.videoItem</upnp:class><res  protocolInfo="http-get:*:video/quicktime:*" size="0" duration=""
 * resolution=""></res></item></DIDL-Lite>
 * play
 * 0
 * 1
 * getTransportInfo
 * 0
 * getPositionInfo
 * 0
 * getTransportInfo
 * 0
 * getPositionInfo
 * 0
 * getTransportInfo
 * 0
 * seek
 * 0
 * REL_TIME
 * 00:00:00
 * getTransportInfo
 * 0
 * getPositionInfo
 * 0
 */
public class MyAvTransportService extends AVTransportService {

    public MyAvTransportService(Class stateMachineDefinition, Class initialState) {
        super(stateMachineDefinition, initialState);
    }

    public MyAvTransportService(Class stateMachineDefinition, Class initialState, Class transportClass) {
        super(stateMachineDefinition, initialState, transportClass);
    }

    @Override
    public void setAVTransportURI(UnsignedIntegerFourBytes instanceId, String currentURI, String currentURIMetaData)
        throws AVTransportException {
        System.out.println("setAVTransportURI");
        System.out.println(instanceId);
        System.out.println(currentURI);
        System.out.println(currentURIMetaData);
        super.setAVTransportURI(instanceId, currentURI, currentURIMetaData);
    }

    @Override
    public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId, String nextURI, String nextURIMetaData)
        throws AVTransportException {
        System.out.println("setNextAVTransportURI");
        System.out.println(instanceId);
        System.out.println(nextURI);
        System.out.println(nextURIMetaData);
        super.setNextAVTransportURI(instanceId, nextURI, nextURIMetaData);
    }

    @Override
    public void setPlayMode(UnsignedIntegerFourBytes instanceId, String newPlayMode) throws AVTransportException {
        System.out.println("setPlayMode");
        System.out.println(instanceId);
        System.out.println(newPlayMode);
        super.setPlayMode(instanceId, newPlayMode);
    }

    @Override
    public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId, String newRecordQualityMode)
        throws AVTransportException {
        System.out.println("setRecordQualityMode");
        System.out.println(instanceId);
        System.out.println(newRecordQualityMode);
        super.setRecordQualityMode(instanceId, newRecordQualityMode);
    }

    @Override
    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("getMediaInfo");
        System.out.println(instanceId);
        return super.getMediaInfo(instanceId);
    }

    @Override
    public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("getTransportInfo");
        System.out.println(instanceId);
        return super.getTransportInfo(instanceId);
    }

    @Override
    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("getPositionInfo");
        System.out.println(instanceId);
        return super.getPositionInfo(instanceId);
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("getDeviceCapabilities");
        System.out.println(instanceId);
        return super.getDeviceCapabilities(instanceId);
    }

    @Override
    public TransportSettings getTransportSettings(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("getTransportSettings");
        System.out.println(instanceId);
        return super.getTransportSettings(instanceId);
    }

    @Override
    public void stop(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("stop");
        System.out.println(instanceId);
        super.stop(instanceId);
    }

    @Override
    public void play(UnsignedIntegerFourBytes instanceId, String speed) throws AVTransportException {
        System.out.println("play");
        System.out.println(instanceId);
        System.out.println(speed);
        super.play(instanceId, speed);
    }

    @Override
    public void pause(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("pause");
        System.out.println(instanceId);
        super.pause(instanceId);
    }

    @Override
    public void record(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("record");
        System.out.println(instanceId);
        super.record(instanceId);
    }

    @Override
    public void seek(UnsignedIntegerFourBytes instanceId, String unit, String target) throws AVTransportException {
        System.out.println("seek");
        System.out.println(instanceId);
        System.out.println(unit);
        System.out.println(target);
        super.seek(instanceId, unit, target);
    }

    @Override
    public void next(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("next");
        System.out.println(instanceId);
        super.next(instanceId);
    }

    @Override
    public void previous(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        System.out.println("previous");
        System.out.println(instanceId);
        super.previous(instanceId);
    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerFourBytes instanceId) throws Exception {
        System.out.println("getCurrentTransportActions");
        System.out.println(instanceId);
        return super.getCurrentTransportActions(instanceId);
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        System.out.println("getCurrentInstanceIds");
        return super.getCurrentInstanceIds();
    }


}
