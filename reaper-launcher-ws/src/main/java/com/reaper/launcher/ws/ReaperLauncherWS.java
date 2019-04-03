package com.reaper.launcher.ws;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ReaperLauncherWS {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReaperLauncherWS.class)
                .logStartupInfo(false)
                .run(args);
    }
}
