package tw.com.softleader.ethweb.demo.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import tw.com.softleader.commons.validation.constraints.AssertThat;
import tw.com.softleader.commons.validation.constraints.Latin;
import tw.com.softleader.data.entity.GenericCodeEntity;

/**
 * @see https://github.com/softleader/softleader-framework-docs/wiki/Bean-Validation
 */
@SuppressWarnings("serial")
@Setter
@Getter
@Entity
@Table(name = "DEMO")
@AssertThat(
    value = "this.age > 0 && this.birthday != null && forName('java.time.Period').between(this.birthday, forName('java.time.LocalDate').now()).getYears() == this.age",
    propertyNode = "age", message = "{demo.birthday.and.age.not.match}")
public class Demo extends GenericCodeEntity<Long> {

  @Min(0)
  @Max(130)
  @Column(name = "age")
  private int age;

  @NotNull
  @Column(name = "birthday")
  private LocalDate birthday;

  @Email
  @Column(name = "email")
  private String email;

  @JsonManagedReference("demo_associations")
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "demo")
  private List<DemoAssociation> associations;

  public void addAssociation(DemoAssociation entity) {
    if (this.associations == null) {
      throw new IllegalStateException(
          "If this is the first time inserting the entity, call setAssociations(List) first, otherwise you should select from database before updating the entity");
    }
    this.associations.add(entity);
    if (entity.getDemo() != this) {
      entity.setDemo(this);
    }
  }

  @NotNull
  @Latin
  @Override
  public String getCode() { // You can add more JSR303 on GETTER to the fields defined in super class
    return super.getCode();
  }

}