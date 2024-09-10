package org.meldtech.platform.model.security.settings;


import lombok.Getter;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Getter
public abstract class AbstractSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = Integer.MAX_VALUE;
    private final Map<String, Object> settings;

    protected AbstractSettings(Map<String, Object> settings) {
        Assert.notEmpty(settings, "settings cannot be empty");
        this.settings = Collections.unmodifiableMap(new HashMap<>(settings));
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(String name) {
        Assert.hasText(name, "name cannot be empty");
        return (T) this.getSettings().get(name);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            AbstractSettings that = (AbstractSettings)obj;
            return this.settings.equals(that.settings);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.settings});
    }

    public String toString() {
        return "AbstractSettings {settings=" + this.settings + "}";
    }

    protected abstract static class AbstractBuilder<T extends AbstractSettings, B extends AbstractSettings.AbstractBuilder<T, B>> {
        private final Map<String, Object> settings = new HashMap<>();

        protected AbstractBuilder() {
        }

        public AbstractBuilder<T, B> setting(String name, Object value) {
            Assert.hasText(name, "name cannot be empty");
//            Assert.notNull(value, "value cannot be null");
            this.getSettings().put(name, value);
            return this.getThis();
        }

        public AbstractBuilder<T, B> settings(Consumer<Map<String, Object>> settingsConsumer) {
            settingsConsumer.accept(this.getSettings());
            return this.getThis();
        }

        public abstract T build();

        protected final Map<String, Object> getSettings() {
            return this.settings;
        }

        protected final AbstractBuilder<T, B> getThis() {
            return this;
        }
    }
}
