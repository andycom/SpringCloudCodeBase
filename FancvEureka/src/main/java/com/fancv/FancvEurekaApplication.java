package com.fancv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class FancvEurekaApplication
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(FancvEurekaApplication.class,args);

    }
}
