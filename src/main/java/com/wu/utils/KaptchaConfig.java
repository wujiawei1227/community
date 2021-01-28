package com.wu.utils;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-01-27 11:10
 **/

@Configuration
public class KaptchaConfig {
    @Bean
        public Producer kaptchaProducter(){
            Properties properties=new Properties();
            properties.setProperty("kaptcha.image.width","100");
            properties.setProperty("kaptcha.image.height","40");
            properties.setProperty("kaptcha.textproducer.font.size","32");
            properties.setProperty("kaptcha.textproducer.font.color","0,0,0");
            properties.setProperty("kaptcha.textproducer.char.string","123456789abcdefghijklmnopqrstuvwxyz");
            properties.setProperty("kaptcha.textproducer.cahr.length","4");
            properties.setProperty("kaptcha.noise.imp","com.goole.code.kaptcha.imp.NoNoise");


            DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
            Config config = new Config(properties);
            defaultKaptcha.setConfig(config);
            return defaultKaptcha;
        }

}
