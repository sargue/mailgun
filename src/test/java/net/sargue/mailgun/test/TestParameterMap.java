package net.sargue.mailgun.test;

import com.google.common.collect.Lists;
import net.sargue.mailgun.ParameterMap;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestParameterMap {

    @Test
    public void newMapIsEmpty() {
        ParameterMap m = new ParameterMap();

        assertTrue(m.keySet().isEmpty());
    }

    @Test
    public void putSingle() {
        ParameterMap m = new ParameterMap();
        m.putSingle("aKey", "aValue");

        assertTrue(m.containsKey("aKey"));
        assertTrue(m.getFirst("aKey").isPresent());
        assertEquals("aValue", m.getFirst("aKey").get());
        assertIterableEquals(Collections.singletonList("aValue"), m.getValues("aKey"));
        assertEquals(1, m.keySet().size());
    }

    @Test
    public void add() {
        ParameterMap m = new ParameterMap();
        m.add("aKey", "aValue");

        assertTrue(m.containsKey("aKey"));
        assertTrue(m.getFirst("aKey").isPresent());
        assertEquals("aValue", m.getFirst("aKey").get());
        assertIterableEquals(Collections.singletonList("aValue"), m.getValues("aKey"));
        assertEquals(1, m.keySet().size());

        m.add("aKey", "aValue2");

        assertTrue(m.containsKey("aKey"));
        assertTrue(m.getFirst("aKey").isPresent());
        assertEquals("aValue", m.getFirst("aKey").get());
        assertIterableEquals(Lists.newArrayList("aValue", "aValue2"),
                             m.getValues("aKey"));
        assertEquals(1, m.keySet().size());
    }

    @Test
    public void addAllToEmpty() {
        ParameterMap m = new ParameterMap();
        m.addAll("aKey", Lists.newArrayList("aValue1", "aValue2"));

        assertTrue(m.containsKey("aKey"));
        assertTrue(m.getFirst("aKey").isPresent());
        assertEquals("aValue1", m.getFirst("aKey").get());
        assertIterableEquals(Lists.newArrayList("aValue1", "aValue2"),
                             m.getValues("aKey"));
        assertEquals(1, m.keySet().size());
    }

    @Test
    public void addAllToNonEmpty() {
        ParameterMap m = new ParameterMap();
        m.add("aKey", "aValue");
        m.addAll("aKey", Lists.newArrayList("aValue1", "aValue2"));

        assertTrue(m.containsKey("aKey"));
        assertTrue(m.getFirst("aKey").isPresent());
        assertEquals("aValue", m.getFirst("aKey").get());
        assertIterableEquals(Lists.newArrayList("aValue", "aValue1", "aValue2"),
                             m.getValues("aKey"));
        assertEquals(1, m.keySet().size());
    }

    @Test
    public void remove() {
        ParameterMap m = new ParameterMap();
        m.add("aKey", "aValue");
        m.remove("aKey");

        assertTrue(m.keySet().isEmpty());
        assertFalse(m.containsKey("aKey"));
        assertFalse(m.getFirst("aKey").isPresent());
        assertEquals(0, m.keySet().size());
    }
}
