package com.itrane.boothealth;

import java.io.File;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
//@EnableJms                //TODO: JMS
@EnableScheduling           //タスクスケジューラー
@Import({ DbConfig.class    //データベース設定
    ,MailConfig.class       //メール設定
    //,SecConfig.class      //TODO: セキュリティ設定
})
public class App {

    @Autowired
    private MessageSource ms;

    @Bean
    public AppMsg appMsg() {
        return (AppUtil.msg = new AppMsg(ms));
    }

    @Configuration
    static class AppConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            // --- デフォルトのエラーページを変更
            registry.addViewController("/error").setViewName("error");
            registry.addViewController("/login").setViewName("login");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}