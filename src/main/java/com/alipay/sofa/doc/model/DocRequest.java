package com.alipay.sofa.doc.model;


import java.util.Map;

public class DocRequest {
    private String executionTaskId;
    private Map<String, String> inputs;
    private String submitResultUrl;

    public DocRequest() {
    }

    public String getExecutionTaskId() {
        return this.executionTaskId;
    }

    public Map<String, String> getInputs() {
        return this.inputs;
    }

    public String getSubmitResultUrl() {
        return this.submitResultUrl;
    }


    public void setExecutionTaskId(String executionTaskId) {
        this.executionTaskId = executionTaskId;
    }

    public void setInputs(Map<String, String> inputs) {
        this.inputs = inputs;
    }

    public void setSubmitResultUrl(String submitResultUrl) {
        this.submitResultUrl = submitResultUrl;
    }

    @Override
    public String toString() {
        return "DocRequest{" +
                "executionTaskId='" + executionTaskId + '\'' +
                ", inputs=" + inputs +
                ", submitResultUrl='" + submitResultUrl + '\'' +
                '}';
    }
}