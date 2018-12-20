package pico.erp.user.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.UserId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GroupJoinedUserView {

  GroupId groupId;

  UserId userId;

  String userName;

}
