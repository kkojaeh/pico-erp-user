package pico.erp.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.group.GroupId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserGroupIncludedOrNotView {

  UserId userId;

  GroupId groupId;

  String groupName;

  boolean included;

}
