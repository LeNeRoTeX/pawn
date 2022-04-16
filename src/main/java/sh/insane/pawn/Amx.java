package sh.insane.pawn;

import lombok.extern.log4j.Log4j2;
import org.springframework.scripting.ScriptSource;
import sh.insane.pawn.extension.AmxContext;
import sh.insane.pawn.extension.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

@Log4j2
public class Amx {
    private final AmxContext amxContext;
    private final Collection<Plugin> plugins;
    private final Collection<Script> scripts;

    public Amx() {
        amxContext = new AmxContext();
        plugins = new ArrayList<>();
        scripts = new ArrayList<>();
    }

    public AmxError loadFromFile(String filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            Script script = new Script(filePath, fileContent);

            AmxError validationError = script.validate();

            if(!validationError.equals(AmxError.AMX_ERR_NONE)) {
                log.error("[{}] Run time error {}: \"{}\"", script.getFileLocation(), validationError.getId(), validationError.getReason());
                return validationError;
            }

            scripts.add(script);

            script.executeMain();
        } catch (IOException e) {
            log.error("could not load script from file {}", e.getMessage());
            return AmxError.AMX_ERR_FILE;
        }

        return AmxError.AMX_ERR_NONE;
    }
}
