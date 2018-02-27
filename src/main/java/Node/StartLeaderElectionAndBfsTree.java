package Node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
@Slf4j
public class StartLeaderElectionAndBfsTree implements Runnable{
    @Autowired(required = true)
    @Qualifier("Node/LeaderElectionService/vote")
    private LeaderElectionService.Vote vote;

    @Autowired(required = true)
    @Qualifier("Node/LeaderElectionConfig/electingNewLeader")
    private Semaphore electingNewLeader;

    private ApplicationContext context;

    public StartLeaderElectionAndBfsTree(ApplicationContext context){
        this.context = context;
    }

    @Override
    public void run() {
        Runnable electNewLeader = new ElectNewLeader(context);
        electNewLeader.run();
        try {
            electingNewLeader.acquire();
        } catch (InterruptedException e) {
            log.warn("interrupted while waiting on leader to be elected");
        }
        log.trace("moving onto building bfs tree");
        if(vote.isThisNodeLeader()) {
            Runnable buildBfsTree = new BuildBfsTree(context);
            buildBfsTree.run();
        }
    }
}
