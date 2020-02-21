package com.jimsshom.streamingplayer.upnpservice;

import java.io.IOException;

import com.jimsshom.streamingplayer.eventbus.EventAdapter;
import com.jimsshom.streamingplayer.eventbus.EventBusManager;
import com.jimsshom.streamingplayer.eventbus.EventType;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.lastchange.LastChangeParser;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:06
 */
public class UpnpServiceManager implements Runnable {
    private UpnpService upnpService;
    private MyAvTransportService myAvTransportService = new MyAvTransportService(
        MyRendererStateMachine.class,   // All states
        MyRendererNoMediaPresent.class  // Initial state
    );

    public void initial() {
        registerEventConsumer();
        startServiceThread();
    }

    private void startServiceThread() {
        Thread serverThread = new Thread(this);
        serverThread.setDaemon(false);
        serverThread.start();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerEventConsumer() {
        EventBusManager.addEventAdapter(EventType.BEFORE_SYSTEM_EXIT, new EventAdapter() {
            @Override
            public void process(String param) {
                if (upnpService != null) {
                    upnpService.shutdown();
                }
            }
        });
        EventBusManager.addEventAdapter(EventType.STATUS_CHANGE, new EventAdapter() {
            @Override
            public void process(String param) {
                if ("stopped".equals(param)) {
                    myAvTransportService.onPlayStopped();
                }
            }
        });
        EventBusManager.addEventAdapter(EventType.TOTAL_TIME, new EventAdapter() {
            @Override
            public void process(String param) {
                myAvTransportService.setTotalTime(Long.parseLong(param));
            }
        });
        EventBusManager.addEventAdapter(EventType.PROGRESS_TIME, new EventAdapter() {
            @Override
            public void process(String param) {
                myAvTransportService.setProgressTime(Long.parseLong(param));
            }
        });
    }

    @Override
    public void run() {
        try {
            UpnpLog.log("启动监听服务...");
            upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                createDevice()
            );
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        UpnpLog.log("监听服务启动成功，等待视频客户端投屏...");
    }

    private LocalDevice createDevice()
        throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity =
            new DeviceIdentity(
                UDN.uniqueSystemIdentifier("Demo AVTransport")
            );

        //urn:schemas-upnp-org:device:MediaRenderer:1
        DeviceType type = new DeviceType("schemas-upnp-org", "MediaRenderer");
        //new UDADeviceType("AvTransport", 1);

        DeviceDetails details =
            new DeviceDetails(
                "Friendly AVTransport",
                new ManufacturerDetails("Jimsshom's Home"),
                new ModelDetails(
                    "AVTransport 2000",
                    "A test AVTransport",
                    "v1"
                )
            );

        LocalService<MyAvTransportService> service =                                      // DOC:INC1
            new AnnotationLocalServiceBinder().read(MyAvTransportService.class);

        // Service's which have "logical" instances are very special, they use the
        // "LastChange" mechanism for eventing. This requires some extra wrappers.
        LastChangeParser lastChangeParser = new AVTransportLastChangeParser();

        service.setManager(
            new LastChangeAwareServiceManager<MyAvTransportService>(service, lastChangeParser) {
                @Override
                protected MyAvTransportService createServiceInstance() throws Exception {
                    return myAvTransportService;
                }
            }
        );                                                                              // DOC:INC1

        return new LocalDevice(identity, type, details, service);
    }
}
