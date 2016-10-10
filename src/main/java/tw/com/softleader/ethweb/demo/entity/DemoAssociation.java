package tw.com.softleader.ethweb.demo.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import tw.com.softleader.data.entity.GenericEntity;

/**
 * @see https://github.com/softleader/softleader-framework-docs/wiki/Bean-Validation
 */
@SuppressWarnings("serial")
@Setter
@Getter
@Entity
@Table(name = "DEMO_ASSOCIATION")
public class DemoAssociation extends GenericEntity<Long> {

  @JsonBackReference("demo_associations")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEMO_ID")
  private Demo demo;
}