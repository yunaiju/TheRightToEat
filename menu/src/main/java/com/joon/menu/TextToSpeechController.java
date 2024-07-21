package com.joon.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TextToSpeechController {

    @Autowired
    private TextToSpeechService textToSpeechService;

    @GetMapping("/synthesize")
    public ResponseEntity<byte[]> synthesizeText(@RequestParam String text) {
        byte[] audio = textToSpeechService.synthesizeText(text);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.mp3\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audio);
    }
}
