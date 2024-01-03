import org.junit.Test;
import org.junit.runner.RunWith;
import org.lht.springboot.Application;
import org.lht.springboot.entity.ActDeModel;
import org.lht.springboot.service.ActDeModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2024年01月02日 17:10
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class Activiti3warTest {

    @Autowired
    private ActDeModelServiceImpl service;

    @Test
    public void test1() {
        ActDeModel model = service.getById("637244af-f284-405a-a4ab-103046f42641");
        System.out.println(model);
    }
}
