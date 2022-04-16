package sh.insane.pawn;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TestService {

    @PostConstruct
    public void postConstruct() throws Exception {
        System.out.println("post construct");

        /*AmxFile amxFile = new AmxFile("timertest.amx");

        System.out.println("Version: " + amxFile.getHeader().getAmxVersion());

        for(PublicTableEntry publicTableEntry : amxFile.getPublicTable()) {
            System.out.println(publicTableEntry);
        }

        for(NativeTableEntry nativeTableEntry : amxFile.getNativeTable()) {
            System.out.println(nativeTableEntry);
        }*/

        AmxRuntime amxRuntime = new AmxRuntime("timertest.amx");

        //amxRuntime.executeMain();

        amxRuntime.executePublic(1);
    }
}
