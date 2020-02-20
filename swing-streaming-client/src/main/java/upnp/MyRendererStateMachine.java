package upnp;

import org.fourthline.cling.support.avtransport.impl.AVTransportStateMachine;
import org.seamless.statemachine.States;

/**
 * @author xiaohe.yz
 * @date 2020/02/13
 * @time 20:45
 */
@States({
    MyRendererNoMediaPresent.class,
    MyRendererStopped.class,
    MyRendererPlaying.class
})
public interface MyRendererStateMachine extends AVTransportStateMachine {
}
