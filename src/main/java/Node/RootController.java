package Node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class RootController {
    @Autowired
    private RootService rootService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    @Qualifier("Node/NodeConfigurator/thisNodeInfo")
    private ThisNodeInfo thisNodeInfo;

//    @Autowired
//    @Qualifier("Node/WebSocketConnector/sessions")
//    private List<StompSession> sessions;

    @MessageMapping("/topic/leaderElection")
    @SendTo("/topic/leaderElection")
    public Message leaderElection(LeaderElectionMessage message) throws Exception {
        rootService.leaderElection(message);
        //TODO: check round number vs current round number. if greater push onto queue
        return new LeaderElectionResponse(thisNodeInfo.getUid(), message.getSourceUID());
    }

    //TODO make this a real messsage, connect isn't needed
    public void sendLeaderElection() throws MessagingException {
        //method 1 of broadcasting
        thisNodeInfo.getNeighbors().parallelStream().forEach(neighbor -> {
            LeaderElectionMessage message = new LeaderElectionMessage(thisNodeInfo.getUid(), neighbor.getUid());
            template.convertAndSend("/topic/leaderElection", message);
        });
        //method 2 of broadcasting
//        final LeaderElectionMessage leaderElectionMessage = new LeaderElectionMessage(thisNodeInfo.getUid(), 0);
//        sessions.parallelStream().forEach(session -> {
//            session.send("/topic/leaderElection", leaderElectionMessage);
//        });
    }
}