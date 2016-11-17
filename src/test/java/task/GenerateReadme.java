package task;

import ch.jalu.surax.commands.Command;
import ch.jalu.surax.service.CommandHandler;
import com.google.common.io.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Generates the README.md file.
 */
public class GenerateReadme {

    public static void main(String... args) throws IOException {
        String result = "# Utility commands";
        result += "\n### Commands";

        Collection<Command> commands = buildCommands();
        FileConfiguration pluginYml = YamlConfiguration.loadConfiguration(
            GenerateReadme.class.getResourceAsStream("/plugin.yml"));
        for (Command command : commands) {
            final String permission = command.getRequiredPermission() == null
                ? "none"
                : command.getRequiredPermission().getNode();
            result += "\n- **/" + command.getName() + "**: "
                + pluginYml.get("commands." + command.getName() + ".description");
            result += "\n    - Permission: " + permission;
            result += "\n";
        }

        Files.write(result.getBytes(), new File("README.md"));
    }

    private static Collection<Command> buildCommands() {
        return CommandHandler.COMMAND_CLASSES.stream()
            .map(clz -> {
                try {
                    Constructor<? extends Command> ctor = clz.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    return ctor.newInstance();
                } catch (InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            })
            .collect(Collectors.toList());
    }

}
