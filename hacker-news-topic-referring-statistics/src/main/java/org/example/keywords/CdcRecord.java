package org.example.keywords;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdcRecord {

    private DataRecord before;
    private DataRecord after;

    private Operation op;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataRecord {

        private int id;
        private String keyword;
        private String[] category;

    }

    public enum Operation {
        @JsonProperty("c")
        Create,
        @JsonProperty("r")
        Read,
        @JsonProperty("u")
        Update,
        @JsonProperty("d")
        Delete
    }

}
