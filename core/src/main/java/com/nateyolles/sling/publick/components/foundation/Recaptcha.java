package com.nateyolles.sling.publick.components.foundation;

import javax.script.Bindings;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;

import com.nateyolles.sling.publick.services.RecaptchaService;

/**
 * Recaptcha backed class for Sightly component.
 *
 * The reCAPTCHA component uses the reCAPTCHA service to get the
 * site key and the options that Google provides from its service.
 */
public class Recaptcha implements Use {

    /**
     * The current component as a resource.
     */
    private Resource resource;

    /**
     * The widget size: normal(default) or compact.
     */
    private String size;

    /**
     * The widget theme: light(default) or dark.
     */
    private String theme;

    /**
     * The widget type: image(default) or audio.
     */
    private String type;

    /**
     * The reCAPTCHA site key provided by the reCAPTCHA service.
     */
    private String siteKey;

    /**
     * true to show the widget.
     */
    private boolean show;

    /**
     * The resource's size property name.
     */
    private static final String SIZE_PROPERTY = "size";

    /**
     * The resource's theme property name.
     */
    private static final String THEME_PROPERTY = "theme";

    /**
     * The resource's type property name.
     */
    private static final String TYPE_PROPERTY = "type";

    /**
     * The resource's enable property name.
     */
    private static final String ENABLE_PROPERTY = "enable";

    /**
     * The available theme property name, otherwise leave blank
     * to use default "light" theme.
     */
    private static final String THEME_DARK = "dark";

    /**
     * The available type property name, otherwise leave blank
     * to use default "image" type.
     */
    private static final String TYPE_AUDIO = "audio";

    /**
     * The available size property name, otherwise leave blank
     * to use default "normal" size.
     */
    private static final String SIZE_COMPACT = "compact";

    /**
     * Initialization of the component.
     *
     * Reads the resource, gets the properties and site key.
     *
     * Options for "theme" are:
     * <ul>
     * <li>dark
     * <li>light (default)
     * </ul>
     *
     * Options for "type" are:
     * <ul>
     * <li>audio
     * <li>image (default)
     * </ul>
     *
     * Options for "size" are:
     * <ul>
     * <li>compact
     * <li>normal (default)
     * </ul>
     */
    @Override
    public void init(Bindings bindings) {
        SlingScriptHelper scriptHelper = (SlingScriptHelper)bindings.get(SlingBindings.SLING);
        RecaptchaService recaptchaService = scriptHelper.getService(RecaptchaService.class);

        if (recaptchaService == null) {
            show = false;
        } else {
            resource = (Resource)bindings.get(SlingBindings.RESOURCE);

            ValueMap properties = resource.adaptTo(ValueMap.class);
            String sizeProperty = properties.get(SIZE_PROPERTY, String.class);
            String themeProperty = properties.get(THEME_PROPERTY, String.class);
            String typeProperty = properties.get(TYPE_PROPERTY, String.class);
            boolean enableProperty = properties.get(ENABLE_PROPERTY, true);

            boolean enableService = recaptchaService.getEnabled();
            siteKey = recaptchaService.getSiteKey();

            if (enableService && enableProperty && StringUtils.isNotBlank(siteKey)) {
                show = true;

                if (THEME_DARK.equals(themeProperty)) {
                    theme = themeProperty;
                }

                if (TYPE_AUDIO.equals(typeProperty)) {
                    type = typeProperty;
                }

                if (SIZE_COMPACT.equals(sizeProperty)) {
                    size = sizeProperty;
                }
            } else {
                show = false;
            }
        }
    }

    /**
     * Return the widget theme.
     *
     * @return the widget theme.
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Return the widget type.
     *
     * @return the widget type.
     */
    public String getType() {
        return type;
    }

    /**
     * Return the widget size.
     *
     * @return the widget size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Return whether to show the reCAPTCHA form.
     *
     * @return true to show the reCAPTCHA form.
     */
    public boolean getShow() {
        return show;
    }

    /**
     * Return the site key.
     *
     * @return the site key.
     */
    public String getSiteKey() {
        return siteKey;
    }
}