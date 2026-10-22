package za.co.fraud.fraud_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResourceResponse {

    private Integer totalPages;
    private Integer count;
    @JsonProperty("data")
    private List<Object> data;

}
