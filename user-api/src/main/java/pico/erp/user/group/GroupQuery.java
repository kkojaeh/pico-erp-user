package pico.erp.user.group;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.data.GroupId;
import pico.erp.user.data.GroupJoinedUserView;
import pico.erp.user.data.GroupRoleGrantedOrNotView;
import pico.erp.user.data.GroupView;

public interface GroupQuery {

  List<? extends LabeledValuable> asLabels(@NotNull String keyword, long limit);

  List<GroupJoinedUserView> findAllGroupJoinedUser(@NotNull GroupId groupId);

  List<GroupRoleGrantedOrNotView> findAllGroupRoleGrantedOrNot(@NotNull GroupId groupId);

  Page<GroupView> retrieve(@NotNull GroupView.Filter filter, @NotNull Pageable pageable);

}
