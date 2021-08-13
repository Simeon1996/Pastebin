package base.Helpers;

import base.PasteEntity;

import java.time.Instant;

public class PasteEntityFactory {

    public static PasteEntity create(String pasteId, String content, String ip, String title, Instant expireAt) {
        return new PasteEntity(pasteId, content, ip, title, expireAt);
    }

    public static PasteEntity create(long id, String pasteId, String content, String ip, String title, Instant expireAt) {
        return new PasteEntity(id, pasteId, content, ip, title, expireAt);
    }
}
