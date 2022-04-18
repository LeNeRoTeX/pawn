package sh.insane.pawn;

import lombok.extern.log4j.Log4j2;
import sh.insane.pawn.interop.builtin.BuiltInFunctionsPlugin;
import sh.insane.pawn.interop.AmxContext;
import sh.insane.pawn.interop.Plugin;

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

        loadPlugin(new BuiltInFunctionsPlugin());
    }

    public void loadPlugin(Plugin plugin) {
        try {
            plugin.onPluginLoad(amxContext);
            plugins.add(plugin);
        } catch (Exception e) {
            log.error("could not load plugin: {}", e);
        }
    }

    public Script loadFromFile(String filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            Script script = new Script(filePath, fileContent, amxContext);

            AmxError validationError = script.validate();

            if(!validationError.equals(AmxError.AMX_ERR_NONE)) {
                log.error("[{}] Run time error {}: \"{}\"", script.getFileLocation(), validationError.getId(), validationError.getReason());
                return script;
            }

            scripts.add(script);
            script.executeMain();
            return script;
        } catch (IOException e) {
            log.error("could not load script from file {}", e.getMessage());
            return null;
        }
    }
}
