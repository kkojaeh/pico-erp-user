package pico.erp.user;

import kkojaeh.spring.boot.component.ComponentBean;
import kkojaeh.spring.boot.component.SpringBootComponent;
import kkojaeh.spring.boot.component.SpringBootComponentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pico.erp.ComponentDefinition;
import pico.erp.shared.SharedConfiguration;
import pico.erp.shared.data.Role;
import pico.erp.user.UserApi.Roles;

@Slf4j
@SpringBootComponent("user")
@EntityScan
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
@SpringBootApplication
@Import(value = {
  SharedConfiguration.class
})
@PropertySource({"classpath:user/password-rules.properties"})
public class UserApplication implements ComponentDefinition {

  public static void main(String[] args) {
    new SpringBootComponentBuilder()
      .component(UserApplication.class)
      .run(args);
  }

  @Override
  public Class<?> getComponentClass() {
    return UserApplication.class;
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
  @ComponentBean(host = false)
  public Role userAccessorRole() {
    return Roles.USER_ACCESSOR;
  }

  @Bean
  @ComponentBean(host = false)
  public Role userManagerRole() {
    return Roles.USER_MANAGER;
  }

}
