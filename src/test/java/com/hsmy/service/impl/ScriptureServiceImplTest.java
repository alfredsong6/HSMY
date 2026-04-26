package com.hsmy.service.impl;

import com.hsmy.entity.Scripture;
import com.hsmy.mapper.ScriptureMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptureServiceImplTest {

    @Mock
    private ScriptureMapper scriptureMapper;

    @InjectMocks
    private ScriptureServiceImpl service;

    @Test
    void checkScriptureAvailable_allowsReviewStatus() {
        Scripture scripture = new Scripture();
        scripture.setId(200L);
        scripture.setStatus(3);

        when(scriptureMapper.selectById(200L)).thenReturn(scripture);

        assertTrue(service.checkScriptureAvailable(200L));
    }
}
