/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi;

public abstract class AbstractDictionary implements Dictionary {
    private String _name;
    private String _provider;
    private String _language;
    private String _version;

    protected AbstractDictionary(
            String name, String provider, String language, String version) {
        _name = name;
        _provider = provider;
        _language = language;
        _version = version;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        if (_name != null) {
            throw new RuntimeException("name cannot be reset");
        }
        _name = name;
    }

    public String getProvider() {
        return _provider;
    }

    public void setProvider(String provider) {
        if (_provider != null) {
            throw new RediRuntimeException("provider cannot be reset");
        }
        _provider = provider;
    }

    public String getLanguage() {
        return _language;
    }

    public void setLanguage(String language) {
        if (_language != null) {
            throw new RuntimeException("language cannot be reset");
        }
        _language = language;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        if (_version != null) {
            throw new RediRuntimeException("version cannot be reset");
        }
        _version = version;
    }
}