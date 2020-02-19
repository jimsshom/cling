package vlcdemo;

/**
 * @author jimsshom
 * @date 2020/02/19
 * @time 14:45
 */
public interface MediaPlayerEventListener {
    void onProgressChange(long second);
    void onTotalTimeChange(long second);
    void onStateChange(MediaPlayerState state);
}
