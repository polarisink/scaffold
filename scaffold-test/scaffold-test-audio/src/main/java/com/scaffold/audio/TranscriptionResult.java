package com.scaffold.audio;

import io.swagger.v3.core.util.AnnotationsUtils;

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
