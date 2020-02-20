package eventbus;

/**
 * @author jimsshom
 * @date 2020/02/20
 * @time 16:07
 */

/**
 * 所有消息的消息体均为一个string参数，具体定义如下注释
 */
public enum EventType {
    START_NEW_URL,//URL

    PLAY_OR_PAUSE,//null
    STOP,//null
    SEEK_BY_TIME,//long millisecond
    SKIP_BY_TIME,//long millisecond
    SET_VOLUME,//int 0-100

    STATUS_CHANGE,//playing/paused/stopped
    PROGRESS_TIME,//long millisecond
    TOTAL_TIME,//long millisecond

    LOG,//text to be shown
    BEFORE_SYSTEM_EXIT,//null
}
