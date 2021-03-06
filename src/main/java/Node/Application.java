package Node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) {
        if(log.isTraceEnabled()) {
            log.trace("before running application");
        }
        ApplicationContext context = SpringApplication.run(Application.class, args);
        DoLeaderElectionAndBfsTree doLeaderElectionAndBfsTree = context.getBean(DoLeaderElectionAndBfsTree.class);
        Thread thread = new Thread(doLeaderElectionAndBfsTree);
        if(log.isTraceEnabled()) {
            log.trace("before running sendLeaderElection thread");
        }
        thread.start();
    }
}