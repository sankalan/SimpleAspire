package dev.sankalan.SimpleAspire.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import jakarta.servlet.annotation.WebListener;

@Configuration
@WebListener
public class CustomRequestContextListener extends RequestContextListener {

}
