package pico.erp.user;

import com.ebay.xcelite.annotations.Column;
import com.ebay.xcelite.annotations.Row;
import com.ebay.xcelite.converters.ColumnValueConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.data.UserId;
import pico.erp.user.department.DepartmentXportData.DepartmentIdColumnValueConverter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Row(colsOrder = {"id", "name", "email", "mobilePhoneNumber", "position", "departmentId",
  "password"})
public class UserXportData {

  @Column
  String password;

  @Column(converter = UserIdColumnValueConverter.class)
  UserId id;

  @Column
  String name;

  @Column
  String email;

  @Column
  String mobilePhoneNumber;

  @Column
  String position;

  @Column(converter = DepartmentIdColumnValueConverter.class)
  DepartmentId departmentId;

  public static class UserIdColumnValueConverter implements ColumnValueConverter<String, UserId> {

    @Override
    public UserId deserialize(String value) {
      return UserId.from(value);
    }

    @Override
    public String serialize(UserId value) {
      return value.getValue();
    }

  }


}
