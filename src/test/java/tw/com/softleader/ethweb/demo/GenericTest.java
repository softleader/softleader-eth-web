package tw.com.softleader.ethweb.demo;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import tw.com.softleader.domain.config.DefaultDomainConfiguration;
import tw.com.softleader.ethweb.config.DataSourceConfig;
import tw.com.softleader.ethweb.config.EthereumConfig;
import tw.com.softleader.ethweb.config.ServiceConfig;

@WithMockUser("test")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional
@ContextConfiguration(classes = {
    EthereumConfig.class,
    ServiceConfig.class,
    DataSourceConfig.class,
    DefaultDomainConfiguration.class
})
public class GenericTest {

}
