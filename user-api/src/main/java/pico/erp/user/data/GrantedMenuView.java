package pico.erp.user.data;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class GrantedMenuView implements Comparable<GrantedMenuView> {

  String id;

  String url;

  String icon;

  String name;

  @Builder.Default
  List<GrantedMenuView> children = new LinkedList<>();

  @Override
  public int compareTo(GrantedMenuView view) {
    return this.getName().compareTo(view.getName());
  }
}
