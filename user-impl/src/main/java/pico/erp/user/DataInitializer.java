package pico.erp.user;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import pico.erp.shared.ApplicationInitializer;
import pico.erp.user.DepartmentRequests.CreateRequest;

@Transactional
@Configuration
@Profile({"!development", "!production"})
public class DataInitializer implements ApplicationInitializer {

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private DepartmentService departmentService;

  @Lazy
  @Autowired
  private GroupService groupService;


  @Autowired
  private DataProperties dataProperties;

  @Override
  public void initialize() {
    dataProperties.users.forEach(userService::create);
    dataProperties.departments.forEach(departmentService::create);
    dataProperties.groups.forEach(groupService::create);
  }

  @Data
  @Configuration
  @ConfigurationProperties("data")
  public static class DataProperties {

    List<UserRequests.CreateRequest> users = new LinkedList<>();

    List<CreateRequest> departments = new LinkedList<>();

    List<GroupRequests.CreateRequest> groups = new LinkedList<>();

  }

}
