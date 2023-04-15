package com.bot.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Service
@Log
public class BrowserConfig {

    @Value("${selenium.headless}")
    private boolean headLessBrowser = false;
    private final ResourceLoader resourceLoader;

    public final List<String> browserOptions = new ArrayList<>();

    public BrowserConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        browserOptions.addAll(Arrays.asList("--no-sandbox",
                "--disable-dev-shm-usage",
                "--headless",
//                "--remote-debugging-port=9222",
                "--remote-allow-origins=*"));
        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
    }


	public WebDriver getChromeDriver(String userProfileId) throws IOException {
        var profileUrl = MessageFormat.format("browser-profile/{0}", userProfileId);
        var profilePath = new ClassPathResource(profileUrl).getPath();

        var customOption = new ArrayList<String>();

        if (!this.headLessBrowser) {
            customOption.addAll(this.browserOptions.stream().filter(it -> !it.contains("headless")).toList());
        } else {
            log.log(Level.INFO,"BrowserConfig >> open chrome in headless mode");
            customOption.addAll(this.browserOptions);
        }
        customOption.add(MessageFormat.format("{0}{1}", "--user-data-dir=", profilePath));
        var chromeOption = new ChromeOptions();
        var extensions = getResourceFiles("browser-plugin");
        if (CollectionUtils.isNotEmpty(extensions)) {
            chromeOption.addExtensions(extensions);
        }
        chromeOption.addArguments(customOption);
        return new ChromeDriver(chromeOption);
    }

    public List<File> getResourceFiles(String path) throws IOException {
        var resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("classpath*:" + path + "/**");
        List<File> files = new ArrayList<>();
        for (var resource : resources) {
            var file = convertInputStreamToFile(resource.getInputStream(), resource.getFilename());
            files.add(file);
        }
        return files;
    }

    public File convertInputStreamToFile(InputStream inputStream, String fileName) throws IOException {
        var tempFile = Files.createTempFile(fileName, "");
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        File file = tempFile.toFile();
        file.deleteOnExit();
        return file;
    }

}
