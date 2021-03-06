package pico.erp.user;

import kkojaeh.spring.boot.component.SpringBootComponentReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.user.UserRequests.CreateRequest;
import pico.erp.user.group.GroupQuery;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.group.GroupRequests.AddUserRequest;
import pico.erp.user.group.GroupRequests.GrantRoleRequest;
import pico.erp.user.group.GroupService;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class AdministratorInitializer implements
  ApplicationListener<SpringBootComponentReadyEvent> {

  @Autowired
  private UserService userService;

  @Autowired
  private GroupService groupService;

  @Autowired
  private UserQuery userQuery;

  @Autowired
  private GroupQuery groupQuery;

  @Override
  public void onApplicationEvent(SpringBootComponentReadyEvent event) {
    CreateRequest user = superAdmin();
    GroupRequests.CreateRequest group = superAdminGroup();
    if (!userService.exists(user.getId())) {
      userService.create(user);
      userQuery.findAllUserRoleGrantedOrNot(user.getId())
        .stream()
        .filter(v -> !v.isGranted())
        .map(v -> new UserRequests.GrantRoleRequest(v.getUserId(), v.getRoleId()))
        .forEach(data -> userService.grantRole(data));
      if (!groupService.exists(group.getId())) {
        groupService.create(group);
      }
      groupQuery.findAllGroupRoleGrantedOrNot(group.getId())
        .stream()
        .filter(v -> !v.isGranted())
        .map(v -> new GrantRoleRequest(v.getGroupId(), v.getRoleId()))
        .forEach(data -> groupService.grantRole(data));
      if (!groupService.hasUser(group.getId(), user.getId())) {
        groupService.addUser(new AddUserRequest(group.getId(), user.getId()));
      }
    }
  }

  @Bean
  @ConfigurationProperties("super-admin")
  public CreateRequest superAdmin() {
    return new CreateRequest();
  }

  @Bean
  @ConfigurationProperties("super-admin-group")
  public GroupRequests.CreateRequest superAdminGroup() {
    return new GroupRequests.CreateRequest();
  }


}
