package com.luxx.log.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuditLog {
    @JsonProperty("log_type")
    private String logType = "audit_log";

    @JsonProperty("request_type")
    private String requestType = "service-backend";

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("source_ip")
    private String sourceIp;

    @JsonProperty("request_uri")
    private String requestUri;

    @JsonProperty("request_params")
    private String requestParams;

    @JsonProperty("request_method")
    private String requestMethod;

    @JsonProperty("request_body")
    private String requestBody;

    @JsonProperty("request_time")
    private String requestTime;

    @JsonProperty("response_http_code")
    private int responseHttpCode;

    @JsonProperty("response_body_code")
    private int responseBodyCode;

    @JsonProperty("response_body_msg")
    private String responseBodyMsg;
}
