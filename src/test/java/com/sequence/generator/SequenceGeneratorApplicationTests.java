package com.sequence.generator;

import com.sequence.generator.service.SequenceGenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SequenceGeneratorApplicationTests {

    @Autowired
    SequenceGenService sequenceGenService;

    @Test
    public void contextLoads() {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                String s = sequenceGenService.doWork("192.168.1.1");
                System.out.println("id => " + s);
            }).start();
        }
    }

}
