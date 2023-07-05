package dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class HitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private ZonedDateTime created;
}
