package fm.radiokids.models;

import java.util.HashMap;
import java.util.Map;

public class Excerpt {
    private String rendered;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getRendered() {
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }



}
