package tw.com.softleader.ethweb.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tw.com.softleader.ethweb.config.DataSourceConfig;
import tw.com.softleader.ethweb.config.ServiceConfig;
import tw.com.softleader.ethweb.demo.entity.DemoAssociation;
import tw.com.softleader.ethweb.demo.entity.Demo;
import tw.com.softleader.domain.config.DefaultDomainConfiguration;
import tw.com.softleader.domain.exception.AlreadyExistException;
import tw.com.softleader.domain.exception.OutOfDateException;

@WithMockUser("demo")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ServiceConfig.class, DataSourceConfig.class, DefaultDomainConfiguration.class})
@Transactional
public class DemoServiceTest {

  @Autowired
  private DemoService demoService;

  /**
   * normal insert
   */
  @Test
  public void testInsert() throws Exception {
    final Demo entity = new Demo();
    entity.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
    entity.setAge(10);
    entity.setBirthday(LocalDate.now().minusYears(entity.getAge()));
    final Demo saved = demoService.save(entity);
    Assert.assertEquals(entity, saved);
    Assert.assertNotNull(entity.getId());
    Assert.assertNotNull(entity.getCreatedBy());
    Assert.assertEquals("demo", entity.getCreatedBy());
    Assert.assertNotNull(entity.getCreatedTime());
    Assert.assertNotNull(entity.getModifiedBy());
    Assert.assertEquals("demo", entity.getModifiedBy());
    Assert.assertNotNull(entity.getModifiedTime());
  }

  /**
   * insert duplicate code, expected {@link AlreadyExistException}
   */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  @Test(expected = AlreadyExistException.class)
  public void testDuplicateSave() {
    final Demo entity = new Demo();
    entity.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
    entity.setAge(10);
    entity.setBirthday(LocalDate.now().minusYears(entity.getAge()));
    demoService.save(entity);
    Assert.assertNotNull(entity.getId());

    entity.setId(null);
    demoService.save(entity);
  }

  /**
   * If input's modifiedTime less than database's modifiedTime, an {@link OutOfDateException} should
   * be thrown
   */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  @Test(expected = OutOfDateException.class)
  public void testOutOfDateSave() {
    Demo entity = new Demo();
    entity.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
    entity.setAge(10);
    entity.setBirthday(LocalDate.now().minusYears(entity.getAge()));
    entity = demoService.save(entity);

    entity.setModifiedTime(entity.getModifiedTime().minusSeconds(10));
    demoService.save(entity);
  }

  /**
   * JSR-303 validation: the period between now and birthday should equals to age
   */
  @Test(expected = ConstraintViolationException.class)
  public void testAgeAndBirthdayViolation() {
    final Demo entity = new Demo();
    entity.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
    entity.setAge(10);
    entity.setBirthday(LocalDate.now());

    try {
      demoService.save(entity);
    } catch (final ConstraintViolationException ex) {
      Assert.assertEquals(1, ex.getConstraintViolations().size()); // expected only 1 error
      Assert.assertFalse(
          ex.getConstraintViolations().iterator().next().getMessage().startsWith("demo.")); // the error message should be translated
      throw ex;
    }
  }

  /**
   * One to many
   */
  @Test
  public void testAssociations() throws Exception {
    final Demo entity = new Demo();
    entity.setCode(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
    entity.setAge(10);
    entity.setBirthday(LocalDate.now().minusYears(entity.getAge()));
    entity.setAssociations(new ArrayList<>());
    final int associationSize = 2;
    IntStream.range(0, associationSize).forEach(i -> {
      entity.addAssociation(new DemoAssociation());
    });
    final Demo saved = demoService.save(entity);
    Assert.assertNotNull(saved.getAssociations());
    Assert.assertEquals(associationSize, saved.getAssociations().size());
    saved.getAssociations().forEach(a -> {
      Assert.assertNotNull(a.getId());
      Assert.assertNotNull(a.getDemo());
    });
  }

}