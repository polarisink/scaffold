package com.scaffold.audio;

public class TranscriptionResult {
    public static TranscriptionResult failure(String s) {
        return null;
    }

    public static TranscriptionResult success(String transcript) {

        return null;
    }

    public boolean isSuccess() {
        return false;
    }

    public String getErrorMessage() {
        return null;
    }

    public String getText() {
        return null;
    }
}
