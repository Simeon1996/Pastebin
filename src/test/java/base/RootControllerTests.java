package base;

import base.Helpers.PasteEntityFactory;
import base.Helpers.PasteRequestBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.naming.LimitExceededException;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RootControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RootService service;

    @Test
    public void testGet() throws Exception {
        long id = 1;
        String content = "test content <b>dummy</b>";
        String pasteId = "AbcdE";
        String ip = "37.64.13.71";

        when(service.getById(1)).thenReturn(PasteEntityFactory.create(id, pasteId, content, ip, null, null));

        mockMvc.perform(get("/pastes/1"))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.pasteId").value(pasteId))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title", Matchers.nullValue()))
                .andExpect(jsonPath("$.expireAt", Matchers.nullValue()));
    }

    @Test
    public void testGetWithContentAndTitle() throws Exception {
        long id = 1;
        String content = "test content <b>dummy</b>";
        String title = "test content <b>dummy</b>";
        String pasteId = "AbcdE";
        String ip = "37.64.13.71";

        when(service.getById(1)).thenReturn(PasteEntityFactory.create(id, pasteId, content, ip, title, null));

        mockMvc.perform(get("/pastes/1"))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.pasteId").value(pasteId))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.expireAt", Matchers.nullValue()));
    }

    @Test
    public void testGetWithContentAndTitleAndExpireAt() throws Exception {
        long id = 1;
        String content = "test content <b>dummy</b>";
        String title = "test";
        Instant expireAt = Instant.now();
        String pasteId = "AbcdE";
        String ip = "37.64.13.71";

        when(service.getById(1)).thenReturn(PasteEntityFactory.create(id, pasteId, content, ip, title, expireAt));

        mockMvc.perform(get("/pastes/1"))
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.pasteId").value(pasteId))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.expireAt").isNotEmpty());
    }

    @Test
    public void testGetByUnknownId() throws Exception
    {
        String expectedErrorMessage = "Element not found.";
        when(service.getById(1)).thenThrow(new NoSuchElementException(expectedErrorMessage));

        mockMvc.perform(get("/pastes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/pastes/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testGetAllByPasteId() throws Exception {
        String ip = "127.0.0.1";

        List<PasteEntity> pastes = new ArrayList<>(3);

        pastes.add(PasteEntityFactory.create(1, "AbcdE", "http://dummy-url.com/aaa", "127.0.0.1", "test1", null));
        pastes.add(PasteEntityFactory.create(2, "AbcdF", "http://dummy-url.com/fff", "127.0.0.1", "test2", null));
        pastes.add(PasteEntityFactory.create(3, "AbcdG", "http://dummy-url.com/ggg", "127.0.0.1", "test3", null));

        when(service.getAll(ip)).thenReturn(pastes);

        mockMvc.perform(get("/pastes"))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].*", hasSize(5)))
                .andExpect(jsonPath("$.[0].id").value(pastes.get(0).getId()))
                .andExpect(jsonPath("$.[0].pasteId").value(pastes.get(0).getPasteId()))
                .andExpect(jsonPath("$.[0].content").value(pastes.get(0).getContent()))
                .andExpect(jsonPath("$.[0].expireAt", Matchers.nullValue()))
                .andExpect(jsonPath("$.[0].title").value(pastes.get(0).getTitle()))
                .andExpect(jsonPath("$.[1].id").value(pastes.get(1).getId()))
                .andExpect(jsonPath("$.[1].pasteId").value(pastes.get(1).getPasteId()))
                .andExpect(jsonPath("$.[1].content").value(pastes.get(1).getContent()))
                .andExpect(jsonPath("$.[1].expireAt", Matchers.nullValue()))
                .andExpect(jsonPath("$.[1].title").value(pastes.get(1).getTitle()))
                .andExpect(jsonPath("$.[2].id").value(pastes.get(2).getId()))
                .andExpect(jsonPath("$.[2].pasteId").value(pastes.get(2).getPasteId()))
                .andExpect(jsonPath("$.[2].content").value(pastes.get(2).getContent()))
                .andExpect(jsonPath("$.[2].expireAt", Matchers.nullValue()))
                .andExpect(jsonPath("$.[2].title").value(pastes.get(2).getTitle()));
    }

    @Test
    public void testGetAllWhileEmptyByPasteId() throws Exception {
        String ip = "127.0.0.1";

        List<PasteEntity> pastes = new ArrayList<>(0);

        when(service.getAll(ip)).thenReturn(pastes);

        mockMvc.perform(get("/pastes"))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    public void testDeleteByValidId() throws Exception
    {
        String ip = "37.64.13.71";

        doNothing().when(service).delete(1, ip);

        mockMvc.perform(delete("/pastes/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteByValidIdAndNotOwnIp() throws Exception
    {
        String ip = "127.0.0.1";
        String expectedErrorMessage = "You are not authorized to perform the action.";

        // Client shouldn't be allowed to perform delete action if the url was created from a different ip address.
        doThrow(new InvalidParameterException("You are not authorized to perform the action.")).when(service).delete(1, ip);

        mockMvc.perform(delete("/pastes/delete/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/pastes/delete/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testDeleteByInvalidId() throws Exception
    {
        String expectedErrorMessage = "Element not found.";
        String ip = "127.0.0.1";

        doThrow(new NoSuchElementException(expectedErrorMessage)).when(service).delete(1, ip);

        mockMvc.perform(delete("/pastes/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(expectedErrorMessage))
                .andExpect(jsonPath("$.description").value("uri=/pastes/delete/1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testCreateSuccessfulWithoutTitle() throws Exception
    {
        String ip = "127.0.0.1";
        String pasteId = "AbcdE";

        PasteRequestBody requestBody = new PasteRequestBody();
        requestBody.setContent("test");

        PasteEntity entity = PasteEntityFactory.create(pasteId, requestBody.getContent(), ip, null, null);

        when(service.create(requestBody, ip)).thenReturn(entity);

        mockMvc.perform(post("/pastes/create").content("{ \"content\": \"test\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.pasteId").value(pasteId))
                .andExpect(jsonPath("$.content").value(requestBody.getContent()))
                .andExpect(jsonPath("$.title", Matchers.nullValue()))
                .andExpect(jsonPath("$.expireAt", Matchers.nullValue()));
    }

    @Test
    public void testCreateSuccessfulWithTitle() throws Exception
    {
        String ip = "127.0.0.1";
        String pasteId = "AbcdE";

        PasteRequestBody requestBody = new PasteRequestBody();
        requestBody.setContent("test");
        requestBody.setTitle("DummyPaste");

        PasteEntity entity = PasteEntityFactory.create(pasteId, requestBody.getContent(), ip, requestBody.getTitle(), null);

        when(service.create(requestBody, ip)).thenReturn(entity);

        mockMvc.perform(post("/pastes/create").content("{ \"content\": \"test\", \"title\": \"DummyPaste\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.pasteId").value(pasteId))
                .andExpect(jsonPath("$.content").value(requestBody.getContent()))
                .andExpect(jsonPath("$.title").value(requestBody.getTitle()))
                .andExpect(jsonPath("$.expireAt", Matchers.nullValue()));
    }

    @Test
    public void testCreateEmptyContent() throws Exception
    {
        PasteRequestBody requestBody = new PasteRequestBody();
        requestBody.setContent("");

        String ip = "127.0.0.1";
        String exception = "Invalid content.";

        when(service.create(requestBody, ip)).thenThrow(new InvalidParameterException(exception));

        mockMvc.perform(post("/pastes/create").content("{ \"content\": \"\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(exception))
                .andExpect(jsonPath("$.description").value("uri=/pastes/create"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testCreateWithNoContent() throws Exception
    {
        mockMvc.perform(post("/pastes/create").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCreateWithInvalidMediaType() throws Exception
    {
        mockMvc.perform(post("/pastes/create").contentType(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testCreateMorePastesThanAllowed() throws Exception
    {
        PasteRequestBody requestBody = new PasteRequestBody();
        requestBody.setContent("test");

        String ip = "127.0.0.1";
        String exception = "The allowed number of pastes that can be added is exceeded for you.";

        when(service.create(requestBody, ip)).thenThrow(new LimitExceededException(exception));

        mockMvc.perform(post("/pastes/create").content("{ \"content\": \"test\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value(exception))
                .andExpect(jsonPath("$.description").value("uri=/pastes/create"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
