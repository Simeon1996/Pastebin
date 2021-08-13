package base;

import base.Helpers.PasteRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import base.Helpers.Utils;

import javax.naming.LimitExceededException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class RootController {

    @Autowired
    private RootService service;

    @GetMapping(value = "/pastes/{id}")
    public ResponseEntity<PasteEntity> get(@PathVariable long id) {
        PasteEntity entity = service.getById(id);
        return ResponseEntity.ok(entity);
    }

    @GetMapping(value = "/pastes")
    public ResponseEntity<List<PasteEntity>> getAll(HttpServletRequest request) {
        String clientIp = Utils.getClientIp(request);
        List<PasteEntity> entity = service.getAll(clientIp);
        return ResponseEntity.ok(entity);
    }

    @PostMapping(value = "/pastes/create", consumes = { "application/json" })
    public ResponseEntity<PasteEntity> create(@RequestBody PasteRequestBody body, HttpServletRequest request) throws LimitExceededException {
        String clientIp = Utils.getClientIp(request);
        return ResponseEntity.ok(service.create(body, clientIp));
    }

    @DeleteMapping(value = "/pastes/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, HttpServletRequest request) {
        String clientIp = Utils.getClientIp(request);
        service.delete(id, clientIp);
        return ResponseEntity.noContent().build();
    }
}
