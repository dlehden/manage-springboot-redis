package com.jesper;

import com.jesper.util.RunnableThreadWebCount;
import com.jesper.util.Timers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		//계산 
		RunnableThreadWebCount runnableThreadWebCount = new RunnableThreadWebCount();
		runnableThreadWebCount.run();
		//타이머 스레드 
		Timers timers = new Timers();
		timers.run();

	}
}
