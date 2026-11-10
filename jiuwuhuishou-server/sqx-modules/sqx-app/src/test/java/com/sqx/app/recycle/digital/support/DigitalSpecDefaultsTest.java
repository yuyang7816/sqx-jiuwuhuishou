package com.sqx.app.recycle.digital.support;

import com.sqx.testsupport.PrivateCtor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DigitalSpecDefaultsTest {

    @Test
    void templateAndLabelsShouldResolveByScene() {
        PrivateCtor.invoke(DigitalSpecDefaults.class);
        assertEquals("奢侈品回收", DigitalSpecDefaults.resolveSceneLabel("luxury"));
        assertEquals("数码回收", DigitalSpecDefaults.resolveSceneLabel("digital"));
        assertFalse(DigitalSpecDefaults.parseSections(DigitalSpecDefaults.getTemplate("luxury")).isEmpty());
        assertFalse(DigitalSpecDefaults.parseSections(DigitalSpecDefaults.getTemplate("other")).isEmpty());
    }
}
