package pico.erp.user.group;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pico.erp.shared.data.LabeledValuable;

public interface GroupQuery {

  List<? extends LabeledValuable> asLabels(@NotNull String keyword, long limit);

  List<GroupJoinedUserView> findAllGroupJoinedUser(@NotNull GroupId groupId);

  List<GroupRoleGrantedOrNotView> findAllGroupRoleGrantedOrNot(@NotNull GroupId groupId);

  Page<GroupView> retrieve(@NotNull GroupView.Filter filter, @NotNull Pageable pageable);

}
