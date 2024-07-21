package com.joon.menu;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SpeechController {
    private final SpeechToTextService speechToTextService;

    public SpeechController(SpeechToTextService speechToTextService) {
        this.speechToTextService = speechToTextService;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) {
        try {
            byte[] audioData = file.getBytes();
            String transcript = transcribeAudioData(audioData);
            return ResponseEntity.ok(transcript);
        } catch (IOException e) {
            e.printStackTrace(); // 예외 메시지 출력
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 모든 예외를 처리
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    private String transcribeAudioData(byte[] audioData) throws IOException {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(48000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioData))
                    .build();
            RecognizeResponse response = speechClient.recognize(recognitionConfig, recognitionAudio);
            List<SpeechRecognitionResult> results = response.getResultsList();
            return results.stream()
                    .flatMap(result -> result.getAlternativesList().stream())
                    .map(alternative -> alternative.getTranscript())
                    .reduce("", (transcript, partial) -> transcript + " " + partial).trim();
        }
    }
}
