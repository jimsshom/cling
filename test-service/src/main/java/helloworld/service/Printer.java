package helloworld.service;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

/**
 * @author xiaohe.yz
 * @date 2020/02/11
 * @time 22:15
 */
@UpnpService(
    serviceId = @UpnpServiceId("Printer"),
    serviceType = @UpnpServiceType(value = "Printer", version = 1)
)
public class Printer {
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private int printCount = 0;
    @UpnpStateVariable(defaultValue = "none", sendEvents = false)
    private String latestWords = "none";

    @UpnpAction
    public void print(@UpnpInputArgument(name = "LatestWords")
                              String newLatestWordsValue) {
        latestWords = newLatestWordsValue;
        printCount += 1;
        System.out.println("New word is coming : " + newLatestWordsValue);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "PrintCount"))
    public int getPrintCount() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        return printCount;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "LatestWords"))
    public String getTarget() {
        return latestWords;
    }
}
