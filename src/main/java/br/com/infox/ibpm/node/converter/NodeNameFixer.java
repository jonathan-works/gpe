package br.com.infox.ibpm.node.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NodeNameFixer {
	
    private NodeNameFixer() {
    }
    
    public static String fixCharsInNodeName(String name) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9ÁáÀàÃãÂâÉéÊêÍíÓóÔôÕõÚúçÇ\\s]");
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
