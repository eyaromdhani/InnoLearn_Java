package Services;

import Entities.ExternalOffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServiceExternalOffer {

    private final String BASE_URL = "https://www.themuse.com/api/public/jobs";

    public List<ExternalOffer> fetchInternships(String category, String location) {
        List<ExternalOffer> offers = new ArrayList<>();
        try {
            StringBuilder urlStr = new StringBuilder(BASE_URL);
            urlStr.append("?level=Internship");
            urlStr.append("&page=1");
            
            if (category != null && !category.isEmpty()) {
                urlStr.append("&category=").append(URLEncoder.encode(category, StandardCharsets.UTF_8));
            }
            if (location != null && !location.isEmpty()) {
                urlStr.append("&location=").append(URLEncoder.encode(location, StandardCharsets.UTF_8));
            }

            URL url = new URL(urlStr.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);
                    ExternalOffer offer = new ExternalOffer();
                    offer.setTitle(obj.getString("name"));
                    offer.setCompany(obj.getJSONObject("company").getString("name"));
                    
                    JSONArray locations = obj.getJSONArray("locations");
                    offer.setLocation(locations.length() > 0 ? locations.getJSONObject(0).getString("name") : "Remote");
                    
                    offer.setUrl(obj.getJSONObject("refs").getString("landing_page"));
                    offer.setDescription(obj.getString("contents").replaceAll("<[^>]*>", "").substring(0, Math.min(200, obj.getString("contents").length())) + "...");
                    offer.setLevel("Internship");

                    offers.add(offer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offers;
    }
}
