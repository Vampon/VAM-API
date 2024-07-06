package com.vampon.vamapiinterface.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ChatResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("object")
    private String object;

    @SerializedName("created")
    private long created;

    @SerializedName("result")
    private String result;

    @SerializedName("is_truncated")
    private boolean isTruncated;

    @SerializedName("need_clear_history")
    private boolean needClearHistory;

    @SerializedName("finish_reason")
    private String finishReason;

    @Data
    public static class Usage {
        @SerializedName("prompt_tokens")
        private int promptTokens;

        @SerializedName("completion_tokens")
        private int completionTokens;

        @SerializedName("total_tokens")
        private int totalTokens;
    }

    @SerializedName("usage")
    private Usage usage;
}