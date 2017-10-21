package fm.radiokids.models;

import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

public class Title {
    private String rendered;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getRendered() {
        return Jsoup.parse(rendered).text();
    }

    public void setRendered(String rendered) {
        this.rendered = Jsoup.parse(rendered).text();
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
