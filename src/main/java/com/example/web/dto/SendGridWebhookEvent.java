package com.example.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendGridWebhookEvent {
    private String email;
    private long timestamp;
    private String event;
    
    @JsonProperty("sg_event_id")
    private String sgEventId;
    
    @JsonProperty("sg_message_id")
    private String sgMessageId;
    
    private String reason;
    private String status;
}
