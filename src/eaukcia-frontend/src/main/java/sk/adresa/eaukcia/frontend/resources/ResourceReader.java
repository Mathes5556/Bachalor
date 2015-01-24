package sk.adresa.eaukcia.frontend.resources;


import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

public final class ResourceReader {

    private static final String BUNDLE_NAME = "sk.adresa.eaukcia.frontend.resources.Eaukcia";
    
    static final private Map<Locale, ResourceBundle> resources = Collections.synchronizedMap(new HashMap<Locale, ResourceBundle>());
    static final private Map<Locale, Map<String, String>> cache = Collections.synchronizedMap(new HashMap<Locale, Map<String,String>>());
    
    static {
        
        resources.put(null, ResourceBundle.getBundle(BUNDLE_NAME, new Locale("en", "EN")));
        resources.put(new Locale("sk", "SK"), ResourceBundle.getBundle(BUNDLE_NAME, new Locale("sk", "SK")));
        resources.put(new Locale("en", "EN"), ResourceBundle.getBundle(BUNDLE_NAME, new Locale("en", "EN")));
        prepareCache();
    
    }
    
    private static void prepareCache(){
        for(Locale locale : resources.keySet()){
            cache.put(locale, new HashMap<String, String>());
        }
    }

    public static String getTranslation(Locale locale, String key) {
        ResourceBundle rb = resources.get(locale);
        Map<String, String> rbCache;
                
        if (rb == null) {
            rb = resources.get(null);
            rbCache = cache.get(null);
        } else {
            rbCache = cache.get(locale);
        }

        String ret = rbCache.get(key);
        if(ret == null){
            ret = rb.getString(key);
            rbCache.put(key, ret);
        }
        
        return ret;
        
    }

    public static String getTranslation(String key) {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        return getTranslation(locale, key);
    }
}
