package ch.jalu.surax.domain;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link ToolRecipe}.
 */
public class ToolRecipeTest {

    @Test
    public void shouldInitializeProperly() {
        // given / when
        ToolRecipe.init();

        // then
        Arrays.stream(ToolRecipe.values())
            .forEach(recipe -> assertNotNull(recipe.getResult()));
    }

}