package base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;

@Data
public class PasteEntity {
    private Long id;
    private String pasteId;
    private String content;
    private String title;
    private Instant expireAt;

    @JsonIgnore
    private String ip;

    public PasteEntity(String pasteId, String content, String ip, String title, Instant expireAt) {
        this.pasteId = pasteId;
        this.content = content;
        this.ip = ip;
        this.title = title;
        this.expireAt = expireAt;
    }

    public PasteEntity(long id, String pasteId, String content, String ip, String title, Instant expireAt) {
        this(pasteId, content, ip, title, expireAt);
        this.id = id;
    }
}
