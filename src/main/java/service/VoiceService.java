package service;

import org.vosk.Model;
import org.vosk.Recognizer;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.lang.reflect.Method;
import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for FREE Offline Speech-to-Text using Vosk.
 * Includes Visual Feedback for recording state.
 */
public class VoiceService {

    private static final VoiceService INSTANCE = new VoiceService();
    public static VoiceService getInstance() { return INSTANCE; }

    private TargetDataLine line;
    private boolean isListening = false;
    private String currentLang = "en";
    private final Map<String, Model> models = new HashMap<>();

    private VoiceService() {}

    public void setLanguage(String langCode) {
        this.currentLang = langCode.split("-")[0];
    }

    private Model getModel(String lang) throws java.io.IOException {
        if (!models.containsKey(lang)) {
            models.put(lang, new Model("models/" + lang));
        }
        return models.get(lang);
    }

    /**
     * Starts listening and updates the button style to show recording state.
     */
    public void startListening(TextField target, Button triggerBtn) {
        if (isListening) {
            stopListening();
            return;
        }

        new Thread(() -> {
            try {
                Model model = getModel(currentLang);
                isListening = true;

                // Visual Feedback: Show recording state on button
                Platform.runLater(() -> {
                    triggerBtn.setStyle("-fx-text-fill: #ef4444; -fx-scale-x: 1.2; -fx-scale-y: 1.2;");
                    target.setPromptText("Listening...");
                });

                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                try (Recognizer recognizer = new Recognizer(model, 16000)) {
                    
                    // --- REFLECTION TO CALL acceptWaveform ---
                    Method acceptMethod = Recognizer.class.getMethod("acceptWaveform", byte[].class, int.class);

                    byte[] buffer = new byte[4096];
                    long startTime = System.currentTimeMillis();
                    
                    while (isListening && (System.currentTimeMillis() - startTime < 8000)) { // 8s timeout
                        int bytesRead = line.read(buffer, 0, buffer.length);
                        if (bytesRead > 0) {
                            if ((boolean) acceptMethod.invoke(recognizer, buffer, bytesRead)) {
                                updateUI(target, recognizer.getResult(), true);
                            } else {
                                updateUI(target, recognizer.getPartialResult(), false);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[Voice] Error: " + e.getMessage());
            } finally {
                stopListening();
                // Reset Visuals
                Platform.runLater(() -> {
                    triggerBtn.setStyle("");
                    target.setPromptText("");
                });
            }
        }).start();
    }

    private void updateUI(TextField target, String json, boolean isFinal) {
        String field = isFinal ? "\"text\"" : "\"partial\"";
        if (json != null && json.contains(field)) {
            int start = json.indexOf(":") + 3;
            int end = json.lastIndexOf("\"");
            if (start < end) {
                String text = json.substring(start, end);
                Platform.runLater(() -> {
                    target.setText(text);
                    target.end();
                });
            }
        }
    }

    public void stopListening() {
        isListening = false;
        if (line != null) {
            line.stop();
            line.close();
            line = null;
        }
    }
}
