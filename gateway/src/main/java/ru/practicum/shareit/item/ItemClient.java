package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.shareit.item.dto.CommentDtoGtw;
import ru.practicum.shareit.item.dto.ItemDtoGtw;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItem(String text, Long userId) {
        Map<String, Object> parameters = Map.of("text", text);
            return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> deleteItem(Long itemId, Long userId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> addItem(ItemDtoGtw item, Long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(ItemDtoGtw item, Long userId, Long itemId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> addComment(CommentDtoGtw comment, Long itemId, Long userId) {
        return post("/" + itemId + "/comment",  userId, comment);
    }
}
