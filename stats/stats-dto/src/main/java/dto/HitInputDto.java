package dto;

import lombok.Data;

@Data
public class HitInputDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
