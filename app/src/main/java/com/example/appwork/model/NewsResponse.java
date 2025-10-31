package com.example.appwork.model;

import java.util.List;

public class NewsResponse {
    private String reason;  // 返回说明
    private Result result;  // 返回结果
    private int error_code; // 错误码

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class Result {
        private String stat;
        private List<NewsItem> data;

        public String getStat() {
            return stat;
        }

        public void setStat(String stat) {
            this.stat = stat;
        }

        public List<NewsItem> getData() {
            return data;
        }

        public void setData(List<NewsItem> data) {
            this.data = data;
        }
    }
}
