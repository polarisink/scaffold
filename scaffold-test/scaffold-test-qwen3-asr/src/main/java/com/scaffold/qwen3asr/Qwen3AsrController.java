package com.scaffold.qwen3asr;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/qwen3-asr")
public class Qwen3AsrController {

    private final Qwen3AsrService qwen3AsrService;

    public Qwen3AsrController(Qwen3AsrService qwen3AsrService) {
        this.qwen3AsrService = qwen3AsrService;
    }

    @PostMapping("/transcribe-path")
    public Qwen3AsrResult transcribePath(@ModelAttribute Qwen3AsrRequest request) {
        return qwen3AsrService.transcribe(request);
    }

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Qwen3AsrResult transcribe(
            @RequestPart("audio") MultipartFile audio,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "false") boolean returnTimeStamps
    ) {
        return qwen3AsrService.transcribe(audio, language, returnTimeStamps);
    }

    @ExceptionHandler(Qwen3AsrException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleQwen3AsrException(Qwen3AsrException exception) {
        return Map.of("message", exception.getMessage());
    }
}
