package demo;

import java.util.HashSet;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

/**
 * @author xiaohe.yz
 * @date 2020/02/11
 * @time 21:50
 */
public class ClientMain {

    private static void searchByAVTransport() throws InterruptedException {
        final HashSet<String> remoteSet = new HashSet<>();
        // UPnP discovery is asynchronous, we need a callback
        RegistryListener listener = new RegistryListener() {

            public void remoteDeviceDiscoveryStarted(Registry registry,
                                                     RemoteDevice device) {
                System.out.println(
                    "Discovery started: " + device.getDisplayString()
                );
            }

            public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
                System.out.println(
                    "Discovery failed: " + device.getDisplayString() + " => " + ex
                );
            }

            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                System.out.println(
                    "Remote device available: " + device.getDisplayString()
                );
                for (RemoteService service : device.getServices()) {
                    System.out.println(service.getServiceType().toFriendlyString());
                    //urn:schemas-upnp-org:service:AVTransport:1
                    if (service.getServiceType().equals(new ServiceType("schemas-upnp-org", "AVTransport"))) {
                        remoteSet.add(device.getIdentity().toString());
                        break;
                    }
                }
            }

            public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
                System.out.println(
                    "Remote device updated: " + device.getDisplayString()
                );
            }

            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                System.out.println(
                    "Remote device removed: " + device.getDisplayString()
                );
                remoteSet.remove(device.getIdentity().toString());
            }

            public void localDeviceAdded(Registry registry, LocalDevice device) {
                System.out.println(
                    "Local device added: " + device.getDisplayString()
                );
            }

            public void localDeviceRemoved(Registry registry, LocalDevice device) {
                System.out.println(
                    "Local device removed: " + device.getDisplayString()
                );
            }

            public void beforeShutdown(Registry registry) {
                System.out.println(
                    "Before shutdown, the registry has devices: " + registry.getDevices().size()
                );
            }

            public void afterShutdown() {
                System.out.println("Shutdown of registry complete!");

            }
        };

        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        UpnpService upnpService = new UpnpServiceImpl(listener);

        // Send a search message to all devices and services, they should respond soon
        System.out.println("Sending SEARCH message to all devices...");
        //upnpService.getControlPoint().search(new STAllHeader());
        //upnpService.getControlPoint().search(new ServiceTypeHeader(new ServiceType("schemas-upnp-org", "MediaRenderer")));
        upnpService.getControlPoint().search(new DeviceTypeHeader(new DeviceType("schemas-upnp-org", "MediaRenderer")));
        //urn:schemas-upnp-org:device:MediaRenderer:1

        // Let's wait 10 seconds for them to respond
        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(15000);

        // Release all resources and advertise BYEBYE to other UPnP devices
        System.out.println("Stopping Cling...");
        upnpService.shutdown();

        System.out.println(remoteSet);
        //System.out.println(localSet);
        for (String s : remoteSet) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws Exception {
        searchByAVTransport();
    }
}
