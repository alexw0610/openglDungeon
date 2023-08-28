package engine.loader;

import engine.object.ui.UICharacterTemplate;
import engine.object.ui.UIKerning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.MissingResourceException;

public class UICharacterLoader {

    private static final String RESOURCE_FONT_SUBFOLDER = "font/";
    private static final String DEFAULT_FONT_FILE_EXTENSION = ".fnt";
    private static final String DEFAULT_FONT_FILE = "font";

    public static LinkedList<UICharacterTemplate> loadCharacters() {
        LinkedList<String> fontLines = readfile(RESOURCE_FONT_SUBFOLDER + DEFAULT_FONT_FILE + DEFAULT_FONT_FILE_EXTENSION);
        LinkedList<UICharacterTemplate> uiCharacterTemplates = new LinkedList<>();
        buildUICharacters(fontLines, uiCharacterTemplates);
        return uiCharacterTemplates;
    }

    public static LinkedList<UIKerning> loadKernings() {
        LinkedList<String> fontLines = readfile(RESOURCE_FONT_SUBFOLDER + DEFAULT_FONT_FILE + DEFAULT_FONT_FILE_EXTENSION);
        LinkedList<UIKerning> uiKernings = new LinkedList<>();
        buildUIKernings(fontLines, uiKernings);
        return uiKernings;
    }

    private static void buildUICharacters(LinkedList<String> fontLines, LinkedList<UICharacterTemplate> uiCharacterTemplates) {
        for (String fontLine : fontLines) {
            if (fontLine.startsWith("char")) {
                uiCharacterTemplates.add(buildCharacter(fontLine));
            }
        }
    }

    private static void buildUIKernings(LinkedList<String> fontLines, LinkedList<UIKerning> uiKernings) {
        for (String fontLine : fontLines) {
            if (fontLine.startsWith("kerning")) {
                uiKernings.add(buildKerning(fontLine));
            }
        }
    }

    private static UICharacterTemplate buildCharacter(String fontLine) {
        String[] splits = fontLine.split(" ");
        int id = 0;
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        int xoffset = 0;
        int yoffset = 0;
        int xadvance = 0;
        for (String part : splits) {
            if (part.startsWith("id=")) {
                id = getValue(part);
            } else if (part.startsWith("x=")) {
                x = getValue(part);
            } else if (part.startsWith("y=")) {
                y = getValue(part);
            } else if (part.startsWith("width=")) {
                width = getValue(part);
            } else if (part.startsWith("height=")) {
                height = getValue(part);
            } else if (part.startsWith("xoffset=")) {
                xoffset = getValue(part);
            } else if (part.startsWith("yoffset=")) {
                yoffset = getValue(part);
            } else if (part.startsWith("xadvance=")) {
                xadvance = getValue(part);
            }
        }
        return new UICharacterTemplate(id, x, y, width, height, xoffset, yoffset, xadvance);
    }

    private static UIKerning buildKerning(String fontLine) {
        String[] splits = fontLine.split(" ");
        int first = 0;
        int second = 0;
        int amount = 0;
        for (String part : splits) {
            if (part.startsWith("first=")) {
                first = getValue(part);
            } else if (part.startsWith("second=")) {
                second = getValue(part);
            } else if (part.startsWith("amount=")) {
                amount = getValue(part);
            }
        }
        return new UIKerning(first, second, amount);
    }

    private static int getValue(String part) {
        return Integer.parseInt(part.split("=")[1]);
    }

    private static LinkedList<String> readfile(String file) {
        LinkedList<String> fontLines = new LinkedList<>();
        BufferedReader br;
        InputStream is = UICharacterLoader.class.getClassLoader().getResourceAsStream(file);
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                fontLines.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new MissingResourceException("The specified font file cant be found (or opened) in the resource path." + e, UICharacterLoader.class.getName(), file);
        }
        return fontLines;
    }

}
