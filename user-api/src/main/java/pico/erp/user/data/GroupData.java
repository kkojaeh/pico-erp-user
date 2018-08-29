package pico.erp.user.data;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class GroupData implements Serializable {

  private static final long serialVersionUID = 1L;

  GroupId id;

  String name;

}
