package com.myapp.conversion;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>Created by MontolioV on 28.08.18.
 */
@FacesConverter("com.myapp.conversion.TagNamesConverter")
public class TagNamesConverter implements Converter<List<String>> {
    Pattern patternReplaceSpaces = Pattern.compile("[ ]+");
    Pattern patternClean = Pattern.compile("[^\\d\\p{javaLowerCase}\\p{Punct}]*");
    @Override
    public List<String> getAsObject(FacesContext context, UIComponent component, String value) {
        String lowerCase = value.toLowerCase();
        String[] split = lowerCase.split("[\r\n]+");
        return Arrays.stream(split)
                .map(String::trim)
                .map(s -> patternReplaceSpaces.matcher(s).replaceAll("_"))
                .map(s -> patternClean.matcher(s).replaceAll(""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, List<String> value) {
        StringJoiner sj = new StringJoiner("\n");
        value.forEach(sj::add);
        return sj.toString();
    }
}
