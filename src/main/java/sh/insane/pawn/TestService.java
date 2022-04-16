package sh.insane.pawn;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TestService {

    @PostConstruct
    public void postConstruct() throws Exception {
        //AmxRuntime amxRuntime = new AmxRuntime("timertest.amx");
        //amxRuntime.executeMain();
        //amxRuntime.executePublic(0);

        Amx amx = new Amx();
        amx.loadFromFile("/Users/lukas/Desktop/timertest.amx");
    }
}
