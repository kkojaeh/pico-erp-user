package pico.erp.user;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pico.erp.audit.data.AuditConfiguration;
import pico.erp.shared.ApplicationStarter;
import pico.erp.shared.Public;
import pico.erp.shared.SpringBootConfigs;
import pico.erp.shared.data.Role;
import pico.erp.shared.impl.ApplicationImpl;

@Slf4j
@SpringBootConfigs
@PropertySource({"classpath:user/password-rules.properties"})
public class UserApplication implements ApplicationStarter {

  public static final String CONFIG_NAME = "user/application";

  public static final String CONFIG_NAME_PROPERTY = "spring.config.name=user/application";

  public static final Properties DEFAULT_PROPERTIES = new Properties();

  static {
    DEFAULT_PROPERTIES.put("spring.config.name", CONFIG_NAME);
  }

  public static SpringApplication application() {
    return new SpringApplicationBuilder(UserApplication.class)
      .properties(DEFAULT_PROPERTIES)
      .web(false)
      .build();
  }

  public static void main(String[] args) {
    application().run(args);
  }

  @Override
  public int getOrder() {
    return 1;
  }

  @Override
  public boolean isWeb() {
    return false;
  }

  @Override
  public pico.erp.shared.Application start(String... args) {
    return new ApplicationImpl(application().run(args));
  }

  @Bean
  @Public
  public Role userManagerRole() {
    return ROLE.USER_MANAGER;
  }

  @Component
  @Profile({"development", "production"})
  public static class CacheEvictor {

    @CacheEvict(allEntries = true, cacheNames = {"load-user-by-username"})
    @Scheduled(fixedDelay = 30000)
    public void cacheEvict() {
      // 캐시 삭제 됨 내용 필요 없음
    }
  }

  @Bean
  @Public
  public AuditConfiguration auditConfiguration() {
    return AuditConfiguration.builder()
      .packageToScan("pico.erp.user")
      .entity(ROLE.class)
      .build();
  }

}
