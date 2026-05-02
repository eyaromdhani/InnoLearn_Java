package service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Sends MFA SMS verification codes via Twilio.
 * Configure ACCOUNT_SID, AUTH_TOKEN, FROM_NUMBER before use.
 * In dev mode (DEV_MODE = true), the code is only printed to the console.
 */
public class SmsService {

    // ── Configure these ────────────────────────────────────────────────────
    public static final String ACCOUNT_SID  = System.getenv().getOrDefault("TWILIO_ACCOUNT_SID",  "your_account_sid");
    public static final String AUTH_TOKEN   = System.getenv().getOrDefault("TWILIO_AUTH_TOKEN",   "your_auth_token");
    public static final String FROM_NUMBER  = System.getenv().getOrDefault("TWILIO_PHONE_NUMBER", "+14155238886");

    /** When true, codes are logged to console instead of actually sent. */
    public static final boolean DEV_MODE =
            "1".equals(System.getenv("MFA_KEY_DEV_MODE")) || "true".equals(System.getenv("MFA_KEY_DEV_MODE"));

    static {
        if (!DEV_MODE) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        }
    }

    private static final SmsService INSTANCE = new SmsService();
    public static SmsService getInstance() { return INSTANCE; }
    private SmsService() {}

    /**
     * Sends an 8-digit MFA code via SMS to the given phone number.
     * @param countryCode e.g. "+216"
     * @param phoneNumber e.g. "55123456"
     * @param code        8-digit verification code
     * @return true if sent (or logged in dev mode)
     */
    public boolean sendVerificationCode(String countryCode, String phoneNumber, String code) {
        String fullNumber = formatPhoneNumber(countryCode, phoneNumber);
        String body = "Your InnoLearn verification code is: " + code + ". Expires in 10 minutes.";

        if (DEV_MODE) {
            System.out.println("[DEV SMS] To: " + fullNumber + " | Code: " + code);
            return true;
        }

        try {
            Message msg = Message.creator(
                    new PhoneNumber(fullNumber),
                    new PhoneNumber(FROM_NUMBER),
                    body
            ).create();
            System.out.println("[SMS] Sent to " + fullNumber + " | SID: " + msg.getSid());
            return true;
        } catch (Exception e) {
            System.err.println("[SMS] Failed to send via Twilio (Credits may be over): " + e.getMessage());
            
            // Fallback to UltraMsg (Free Third-Party WhatsApp API)
            System.out.println("[System] Attempting Fallback via UltraMsg Free API...");
            return sendUltraMsgFallback(fullNumber, body);
        }
    }

    /**
     * Fallback to UltraMsg Free WhatsApp API.
     * Visit ultramsg.com to get your Instance ID and Token.
     */
    private boolean sendUltraMsgFallback(String to, String text) {
        try {
            String instanceId = System.getenv().getOrDefault("ULTRAMSG_INSTANCE_ID", "instance172874");
            String token      = System.getenv().getOrDefault("ULTRAMSG_TOKEN", "8q65av01bvgzk6zv");
            
            String urlParameters = "token=" + token + "&to=" + to + "&body=" + java.net.URLEncoder.encode(text, "UTF-8");
            byte[] postData = urlParameters.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            java.net.URL url = new java.net.URL("https://api.ultramsg.com/" + instanceId + "/messages/chat");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            int responseCode = conn.getResponseCode();
            java.io.InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
            java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
            String response = scanner.hasNext() ? scanner.next() : "";

            if (responseCode == 200 && response.contains("\"sent\":\"true\"")) {
                System.out.println("[UltraMsg] Successfully sent fallback code to " + to);
                return true;
            } else {
                System.err.println("[UltraMsg] API Error. Response Code: " + responseCode + " | Body: " + response);
                return false;
            }
        } catch (Exception e) {
            System.err.println("[UltraMsg] Fallback failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Normalizes country code + phone number into E.164 format.
     */
    public String formatPhoneNumber(String countryCode, String phone) {
        if (countryCode == null) countryCode = "";
        if (phone == null) phone = "";

        // Strip non-digit chars from the local number
        phone = phone.replaceAll("[^0-9]", "");
        countryCode = countryCode.replaceAll("[^0-9]", "");

        // Remove leading zeros from local number
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }

        // If phone already starts with the country code, don't double it
        if (phone.startsWith(countryCode) && !countryCode.isEmpty()) {
            return "+" + phone;
        }

        return "+" + countryCode + phone;
    }
}
