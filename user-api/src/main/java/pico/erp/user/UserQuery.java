package pico.erp.user;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.data.UserId;
import pico.erp.user.data.UserRoleGrantedOrNotView;
import pico.erp.user.data.UserView;

public interface UserQuery {

  List<? extends LabeledValuable> asLabels(@NotNull String keyword, long limit);

  List<UserRoleGrantedOrNotView> findAllUserRoleGrantedOrNot(@NotNull UserId userId);

  Page<UserView> retrieve(@NotNull UserView.Filter filter, Pageable pageable);

}
