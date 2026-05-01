package org.example.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class VoiceService {

    public static CompletableFuture<String> listen() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // PowerShell script to use System.Speech
                String script = 
                    "[Console]::OutputEncoding = [System.Text.Encoding]::UTF8;\n" +
                    "$code = @'\n" +
                    "public class SpeechRecognizer {\n" +
                    "    public static string Listen() {\n" +
                    "        try {\n" +
                    "            System.Speech.Recognition.SpeechRecognitionEngine recognizer;\n" +
                    "            try {\n" +
                    "                recognizer = new System.Speech.Recognition.SpeechRecognitionEngine(new System.Globalization.CultureInfo(\\\"fr-FR\\\"));\n" +
                    "            } catch {\n" +
                    "                recognizer = new System.Speech.Recognition.SpeechRecognitionEngine();\n" +
                    "            }\n" +
                    "            using (recognizer) {\n" +
                    "                recognizer.LoadGrammar(new System.Speech.Recognition.DictationGrammar());\n" +
                    "                recognizer.SetInputToDefaultAudioDevice();\n" +
                    "                System.Speech.Recognition.RecognitionResult result = recognizer.Recognize(new System.TimeSpan(0, 0, 5));\n" +
                    "                return result != null ? result.Text : System.String.Empty;\n" +
                    "            }\n" +
                    "        } catch { return System.String.Empty; }\n" +
                    "    }\n" +
                    "}\n" +
                    "'@\n" +
                    "Add-Type -TypeDefinition $code -ReferencedAssemblies System.Speech\n" +
                    "[SpeechRecognizer]::Listen()";

                ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe", 
                    "-ExecutionPolicy", "Bypass", 
                    "-Command", script
                );
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                process.waitFor();

                String result = output.toString().trim();
                System.out.println("Voice Result: " + result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        });
    }
}
