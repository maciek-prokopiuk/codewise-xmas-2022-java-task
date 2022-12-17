package pl.codewise.xmas.task;

import java.time.Instant;

/**
 * You can modify this class
 */
public class Cookie {

    public enum CookieType {
        CHRISTMAS_TREE,
        SANTA_CLAUS_HAT,
        ERROR;
    }

    private final String label;
    private final CookieType cookieType;
    private final Instant createTime;

    public Cookie(String label, CookieType cookieType) {
        this.label = label;
        this.cookieType = cookieType;
        this.createTime = Instant.now();
    }

    public Cookie(String label, CookieType cookieType, Instant createTime) {
        this.label = label;
        this.cookieType = cookieType;
        this.createTime = createTime;
    }

    public String getLabel() {
        return label;
    }

    public CookieType getCookieType() {
        return cookieType;
    }

    public Instant getCreateTime() {
        return createTime;
    }
}
