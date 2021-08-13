package base;

import base.Helpers.PasteEntityFactory;
import base.Helpers.PasteRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import base.Helpers.Utils;

import javax.naming.LimitExceededException;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.*;

@Service
public class RootService {

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Value("${paste.count.per.user}")
    private long pastesCountPerUser;

    @Value("${paste.id.length}")
    private short idLength;

    @Autowired
    private RootRepository repository;

    public PasteEntity create(PasteRequestBody body, String ip) throws LimitExceededException {
        if (!Utils.validateContent(body.getContent())) {
            throw new InvalidParameterException("Invalid content.");
        }

        if (hasUserExceededPastesLimit(ip)) {
            throw new LimitExceededException("The allowed number of pastes that can be added is exceeded for you.");
        }

        String pasteId = generateUniqueId(idLength);

        Instant expireAt = null;

        if (body.getExpireAfter() != null) {
            expireAt = generateExpireAt(body.getExpireAfter());
        }

        PasteEntity entity = PasteEntityFactory.create(pasteId, body.getContent(), ip, body.getTitle(), expireAt);

        boolean saved = repository.save(entity);

        if (!saved) {
            throw new IllegalStateException("Something went wrong during attempts to save.");
        }

        entity.setIp(null);

        return entity;
    }

    public void delete(long id, String ip) {
        PasteEntity entity = getById(id);

        if (!entity.getIp().equals(ip)) {
            throw new InvalidParameterException("You are not authorized to perform the action.");
        }

        repository.delete(id);
    }

    public PasteEntity getById(long id) {
        return repository.getById(id).orElseThrow(() -> new NoSuchElementException("Not found."));
    }

    public PasteEntity getByPasteId(String pasteId) {
        if (pasteId == null || pasteId.isEmpty() || pasteId.length() != idLength) {
            throw new InvalidParameterException("Invalid pasteId.");
        }

        return repository.getByPasteId(pasteId).orElseThrow(() -> new NoSuchElementException("Not found."));
    }

    public boolean hasUserExceededPastesLimit(String ip) {
        return repository.getPastesCountByIp(ip) >= pastesCountPerUser;
    }

    public List<PasteEntity> getAll(String clientIp) {

        if (clientIp == null || clientIp.isEmpty()) {
            throw new InvalidParameterException("Invalid parameter.");
        }

        return repository.getByIp(clientIp);
    }

    private String generateUniqueId(short digits) {
        String id = Utils.generateRandomString(digits);

        boolean pasteExists = repository.checkPasteIdExists(id);

        while (pasteExists) {
            id = Utils.generateRandomString(digits);
            pasteExists = repository.checkPasteIdExists(id);
        }

        return id;
    }

    private Instant generateExpireAt(ExpirationTime expirationTime) {
        return LocalDateTime.now().plus(expirationTime.getTimeValue(), ChronoUnit.valueOf(expirationTime.getTimeUnit().toUpperCase())).toInstant(ZoneOffset.UTC);
    }

    @Scheduled(cron = "0 * * * * *") // every minute
    public void deleteExpiredPastes() {
        System.out.println("HIT");

        int deletedCount = repository.deleteExpiredPastes();

        // log how many pastes were deleted by expiration
        System.out.println("Deleted pastes: " + deletedCount);
    }
}
