package main.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusSettingDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
