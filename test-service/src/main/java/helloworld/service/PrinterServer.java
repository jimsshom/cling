package helloworld.service;

import java.io.IOException;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
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
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

/**
 * @author xiaohe.yz
 * @date 2020/02/11
 * @time 22:20
 */
public class PrinterServer implements Runnable {
    private LocalDevice createDevice()
        throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity =
            new DeviceIdentity(
                UDN.uniqueSystemIdentifier("Demo Printer")
            );

        DeviceType type =
            new UDADeviceType("Printer", 1);

        DeviceDetails details =
            new DeviceDetails(
                "Friendly Printer",
                new ManufacturerDetails("Jimsshom's Home"),
                new ModelDetails(
                    "Printer 2000",
                    "A hello world demo",
                    "v1"
                )
            );

        LocalService<Printer> printerLocalService =
            new AnnotationLocalServiceBinder().read(Printer.class);

        printerLocalService.setManager(
            new DefaultServiceManager(printerLocalService, Printer.class)
        );

        return new LocalDevice(identity, type, details, printerLocalService);

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

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new PrinterServer());
        serverThread.setDaemon(false);
        serverThread.start();
    }
}
