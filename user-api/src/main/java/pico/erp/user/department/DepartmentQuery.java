package pico.erp.user.department;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.data.DepartmentView;

public interface DepartmentQuery {

  List<? extends LabeledValuable> asLabels(@NotNull String keyword, long limit);

  Page<DepartmentView> retrieve(@NotNull DepartmentView.Filter filter,
    @NotNull Pageable pageable);

}
