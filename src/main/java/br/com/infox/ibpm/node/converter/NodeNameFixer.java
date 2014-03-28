package br.com.infox.ibpm.node.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.infox.constants.CharConstants;

public final class NodeNameFixer {
    private NodeNameFixer() {
    }
    
    public static String fixCharsInNodeName(String name) {
        Pattern pattern = Pattern.compile(CharConstants.NODE_NAME_NOT_ALLOWED_CHARS_REGEX);
        Matcher matcher = pattern.matcher(name);
        int originalLength = name.length();
        StringBuilder fixedName = new StringBuilder(name);
        while (matcher.find()) {
           int startIndex = matcher.start();
           int endIndex = matcher.end();
           startIndex = startIndex - (originalLength - fixedName.length());
           endIndex = endIndex - (originalLength - fixedName.length());
           fixedName.delete(startIndex, endIndex);
        }
        return fixedName.toString().trim();
    }
}
