package com.hsmy.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptureMapperXmlTest {

    @Test
    void selectUsableByUserAllowsValidPurchasesWhenScriptureIsOffShelf() throws IOException {
        String xml = new String(
                Files.readAllBytes(Paths.get("src/main/resources/mapper/ScriptureMapper.xml")),
                StandardCharsets.UTF_8
        );
        String sql = extractStatement(xml, "selectUsableByUser")
                .replaceAll("\\s+", " ")
                .trim();

        assertFalse(sql.contains("WHERE s.status = 1 AND s.is_deleted = 0"),
                "status=1 must not be a global filter, or off-shelf purchased scriptures disappear");
        assertTrue(sql.contains("( s.status = 1 AND s.permanent_price = 0 )"),
                "free scriptures should still require status=1");
        assertTrue(sql.contains("OR ( usp.status = 1"),
                "valid user purchases should be allowed independently of scripture status");
    }

    private String extractStatement(String xml, String statementId) {
        String startTag = "<select id=\"" + statementId + "\"";
        int start = xml.indexOf(startTag);
        assertTrue(start >= 0, "missing statement " + statementId);
        int bodyStart = xml.indexOf('>', start);
        int end = xml.indexOf("</select>", bodyStart);
        assertTrue(bodyStart >= 0 && end > bodyStart, "invalid statement " + statementId);
        return xml.substring(bodyStart + 1, end);
    }
}
