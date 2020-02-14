package demo;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.model.AVTransport;

/**
 * @author xiaohe.yz
 * @date 2020/02/11
 * @time 21:56
 */
public class ServiceMain {
    public static void main(String[] args) throws Exception {
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

    private static LocalDevice createDevice() throws ValidationException {
        DeviceIdentity identity =
            new DeviceIdentity(
                UDN.uniqueSystemIdentifier("jimsshom Demo")
            );
        //urn:schemas-upnp-org:device:MediaRenderer:1
        DeviceType type = new DeviceType("schemas-upnp-org", "MediaRenderer");

        DeviceDetails details =
            new DeviceDetails(
                "Friendly Jimsshom Demo",
                new ManufacturerDetails("Jimsshom's Home"),
                new ModelDetails(
                    "BinLight2000",
                    "test AVTransport",
                    "v1"
                )
            );

        LocalService<AVTransport> avTransportService =
            new AnnotationLocalServiceBinder().read(AVTransport.class);

        avTransportService.setManager(
            new DefaultServiceManager(avTransportService, AVTransport.class)
        );

        return new LocalDevice(identity, type, details, avTransportService);

    }

}
