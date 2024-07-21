package com.joon.menu;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SpeechToTextService {
    public String transcribeAudio(byte[] audioData) throws IOException {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
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
