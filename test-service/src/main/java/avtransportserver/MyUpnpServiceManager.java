package avtransportserver;

import java.io.IOException;

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
import vlcdemo.MediaPlayerEventListener;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 20:35
 */
public class MyUpnpServiceManager implements Runnable {
    private MyAvTransportService myAvTransportService = new MyAvTransportService(
        MyRendererStateMachine.class,   // All states
        MyRendererNoMediaPresent.class  // Initial state
    );

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

    /* Several services can be bound to the same device:
    return new LocalDevice(
            identity, type, details, icon,
            new LocalService[] {switchPowerService, myOtherService}
    );
    */

    }

    @Override
    public void run() {
        try {
            final UpnpService upnpService = new UpnpServiceImpl();

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
    }

    public MediaPlayerEventListener getMediaPlayerEventListener() {
        return myAvTransportService;
    }
/*
    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new ServerMain());
        serverThread.setDaemon(false);
        serverThread.start();
    }*/
}
