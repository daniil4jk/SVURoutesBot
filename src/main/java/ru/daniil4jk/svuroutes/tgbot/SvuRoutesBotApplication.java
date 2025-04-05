package ru.daniil4jk.svuroutes.tgbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class SvuRoutesBotApplication {
	private static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		try {
			ctx = SpringApplication.run(SvuRoutesBotApplication.class, args);
		} catch (Throwable e) {
			log.error(e.getLocalizedMessage(), e);
			if (ctx != null) {
				log.error("Ошибка во время работы");
				System.exit(SpringApplication.exit(ctx, () -> -1));
			} else {
				log.error("Ошибка во время запуска");
				System.exit(-1);
			}
		}
	}
}