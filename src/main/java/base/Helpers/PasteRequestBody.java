package base.Helpers;

import base.ExpirationTime;
import lombok.Data;

@Data
public class PasteRequestBody {
    private String content;
    private String title;
    private ExpirationTime expireAfter;
}
