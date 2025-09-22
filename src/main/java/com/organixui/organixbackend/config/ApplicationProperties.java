package com.organixui.organixbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private Uploads uploads = new Uploads();
    private Backend backend = new Backend();

    public Uploads getUploads() { return uploads; }
    public Backend getBackend() { return backend; }

    public static class Uploads {
        private String path;
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }

    public static class Backend {
        private String url;
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
