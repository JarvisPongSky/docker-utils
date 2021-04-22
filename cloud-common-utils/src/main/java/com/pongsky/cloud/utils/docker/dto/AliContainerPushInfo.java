package com.pongsky.cloud.utils.docker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author pengsenhao
 * @create 2021-04-21
 */
@Data
public class AliContainerPushInfo {

    /**
     * pushData
     */
    @JsonProperty("push_data")
    private PushData pushData;

    /**
     * repository
     */
    @JsonProperty("repository")
    private Repository repository;

    @Data
    public static class PushData {

        /**
         * 标签
         */
        @JsonProperty("tag")
        private String tag;

    }

    @Data
    public static class Repository {

        /**
         * 名称
         */
        @JsonProperty("name")
        private String name;

        /**
         * 命名空间
         */
        @JsonProperty("namespace")
        private String namespace;

        /**
         * 地区
         */
        @JsonProperty("region")
        private String region;

    }

}
