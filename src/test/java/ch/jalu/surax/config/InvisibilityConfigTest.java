package ch.jalu.surax.config;

import ch.jalu.injector.testing.BeforeInjecting;
import ch.jalu.injector.testing.DelayedInjectionRunner;
import ch.jalu.injector.testing.InjectDelayed;
import ch.jalu.surax.config.PersistenceFileLoader.PrePersistTracker;
import ch.jalu.surax.domain.DataFolder;
import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link InvisibilityConfig}.
 */
@RunWith(DelayedInjectionRunner.class)
public class InvisibilityConfigTest {

    @InjectDelayed
    private InvisibilityConfig config;
    @InjectDelayed
    private PersistenceFileLoader fileLoader;

    @Mock
    private Logger logger;
    @Mock
    private PrePersistTracker prePersistTracker;

    @DataFolder
    private File dataFolder;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeInjecting
    public void setDataFolder() throws Exception {
        dataFolder = temporaryFolder.newFolder();
        File localConfig = new File(dataFolder, "data.yml");
        File jarDemoConfig = new File(InvisibilityConfigTest.class.getResource("/data.yml").toURI());
        Files.copy(jarDemoConfig, localConfig);
    }

    @Test
    public void shouldLoadCorrectly() {
        // given / when
        Set<String> bobbyIgnored = config.getBlockedPlayers("bobby");
        Set<String> otherIgnored = config.getBlockedPlayers("other"); // doesn't exist

        // then
        assertEquals(bobbyIgnored.size(), 1);
        assertTrue(bobbyIgnored.contains("clarence"));
        assertEquals(otherIgnored.size(), 0);
    }

}