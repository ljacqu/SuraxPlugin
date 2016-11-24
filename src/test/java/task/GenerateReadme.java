package task;

import ch.jalu.surax.Permission;
import ch.jalu.surax.commands.Command;
import ch.jalu.surax.service.CommandHandler;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Generates the README.md file.
 */
public class GenerateReadme {

    public static void main(String... args) throws IOException {
        Collection<Command> commands = buildCommands();
        FileConfiguration pluginYml = YamlConfiguration.loadConfiguration(
            GenerateReadme.class.getResourceAsStream("/plugin.yml"));

        Multimap<Boolean, CommandDescription> descriptions = ArrayListMultimap.create();
        for (Command command : commands) {
            CommandDescription description = new CommandDescription(command, pluginYml);
            descriptions.put(description.isPlayerCommand, description);
        }

        String result = "# Utility commands";
        result += "\n### Commands";
        result += "\n#### Player Commands";
        result += descriptions.get(Boolean.TRUE).stream().map(d -> toString(d))
            .collect(Collectors.joining("\n"));
        result += "\n\n#### Admin Commands";
        result += descriptions.get(Boolean.FALSE).stream().map(d -> toString(d))
            .collect(Collectors.joining("\n"));

        result += "\n\n#### Permissions\n";
        result += Arrays.stream(Permission.values())
            .map(p -> "- " + p.getNode() + ": " + pluginYml.get("permissions." + p.getNode() + ".description"))
            .collect(Collectors.joining("\n"));

        Files.write(result.getBytes(), new File("README.md"));
    }

    private static String toString(CommandDescription description) {
        return "\n- **/" + description.name + "**: " + description.description
            + "\n    - Permission: " + description.permission;
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

    private static final class CommandDescription {
        private String name;
        private String description;
        private String permission;
        private boolean isPlayerCommand;

        CommandDescription(Command command, FileConfiguration configuration) {
            name = command.getName();
            setPermissionFields(command.getRequiredPermission(), configuration);
            description = configuration.getString("commands." + command.getName() + ".description");
        }

        private void setPermissionFields(Permission permission, FileConfiguration configuration) {
            if (permission == null) {
                this.permission = "none";
                this.isPlayerCommand = false;
            } else {
                String node = permission.getNode();
                this.permission = node;
                this.isPlayerCommand = "true".equals(configuration.getString("permissions." + node + ".default"));
            }
        }
    }

}
