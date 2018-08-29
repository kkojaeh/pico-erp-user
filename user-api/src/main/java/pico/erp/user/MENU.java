package pico.erp.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pico.erp.shared.data.Menu;
import pico.erp.shared.data.MenuCategory;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum MENU implements Menu {

  USER_MANAGEMENT("/user", "fas fa-user", MenuCategory.SETTINGS),
  GROUP_MANAGEMENT("/group", "fas fa-users", MenuCategory.SETTINGS),
  DEPARTMENT_MANAGEMENT("/department", "fas fa-sitemap", MenuCategory.SETTINGS);

  String url;

  String icon;

  MenuCategory category;

  public String getId() {
    return name();
  }

}
