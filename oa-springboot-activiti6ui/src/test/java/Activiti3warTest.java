import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lht.springboot.Application;
import org.lht.springboot.entity.ActDeModel;
import org.lht.springboot.service.ActDeModelServiceImpl;
import org.lht.springboot.service.ActivitiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

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

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier(value = "processEngine7")
    private ProcessEngine processEngine;

    @Autowired
    private ActDeModelServiceImpl actDeModelService;

    @Autowired
    private ActivitiServiceImpl activitiService;

    @Autowired
    private RepositoryService repositoryService;

    private String getCookie() {
        String url = "http://localhost:8080/activiti-app/app/authentication";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("j_username", "admin");
        params.add("j_password", "test");
        params.add("_spring_security_remember_me", "true");
        params.add("submit", "Login");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        ResponseEntity<Object> loginResp = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,headers), Object.class);
        String cookie = loginResp.getHeaders().get("Set-Cookie").get(0);
        return cookie;
    }

    @Test
    public void test1() {
        String modelId = "c9e086ad-07f7-4854-808d-c78c876783bc";
        String downloadUrl = "http://localhost:8080/activiti-app/app/rest/models/" + modelId + "/bpmn20";
        Boolean execute = restTemplate.execute(downloadUrl, HttpMethod.GET, new RequestCallback() {
            @Override
            public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
                HttpHeaders headers = clientHttpRequest.getHeaders();
                headers.set("Cookie", getCookie());
            }
        }, new ResponseExtractor<Boolean>() {
            @Override
            public Boolean extractData(ClientHttpResponse clientHttpResponse) throws IOException {
                try {
                    RepositoryService repositoryService = processEngine.getRepositoryService();
                    Deployment deployment = repositoryService.createDeployment()
                            .addInputStream("test01_1", clientHttpResponse.getBody()) //增加bpmn.xml文件
                            .name("test01") //名称
                            .key("1") // key
                            .deploy();
                    System.out.println(deployment.getId());
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        System.out.println(execute);
    }

    @Test
    public void test2() throws Exception {

        String modelId = "c9e086ad-07f7-4854-808d-c78c876783bc";
        String downloadUrl = "http://localhost:8080/activiti-app/app/rest/models/" + modelId + "/bpmn20";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", getCookie());
        ResponseEntity<byte[]> exchange = restTemplate.exchange(downloadUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        if (exchange.getStatusCode() == HttpStatus.OK) {
            RepositoryService repositoryService = processEngine.getRepositoryService();
            Deployment deployment = repositoryService.createDeployment()
                    .addInputStream("test01_1", new ByteArrayInputStream(exchange.getBody())) //增加bpmn.xml文件
                    .name("test01") //名称
                    .key("1") // key
                    .deploy();
            System.out.println(deployment.getId());
        }
    }

    @Test
    public void test3() throws Exception {
        String modelId = "c9e086ad-07f7-4854-808d-c78c876783bc";
        String downloadUrl = "http://localhost:8080/activiti-app/app/rest/models/" + modelId + "/bpmn20";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", getCookie());
        ResponseEntity<byte[]> exchange = restTemplate.exchange(downloadUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
        if (exchange.getStatusCode() == HttpStatus.OK) {
            HttpHeaders headers2 = new HttpHeaders();
            headers2.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:test".getBytes()));
            headers2.set("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            File f = new File("C:\\Users\\HUAWEI\\Desktop" + File.separator + System.currentTimeMillis() + RandomUtil.randomString(3) + ".bpmn20.xml");
            FileOutputStream out = new FileOutputStream(f);
            try {
                IoUtil.write(out,true, exchange.getBody());
                params.add("file", new FileSystemResource(f));
                ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/activiti-rest/service/repository/deployments", HttpMethod.POST, new HttpEntity<>(params, headers2), String.class);
                System.out.println(response.getStatusCode());
                System.out.println(response.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }

    @Test
    public void test4() {
        activitiService.deploy("c9e086ad-07f7-4854-808d-c78c876783bc");
    }

    @Test
    public void test5() {
        ActDeModel actDeModel = actDeModelService.listModelById("c9e086ad-07f7-4854-808d-c78c876783bc");
        Deployment deploy = repositoryService.createDeployment()
                .addInputStream(actDeModel.getName(), new ByteArrayInputStream(actDeModel.getThumbnail())) //增加bpmn.xml文件
                .name(actDeModel.getName())
                .key(actDeModel.getModelKey())
                .deploy();
        System.out.println(deploy.toString());
    }
}
