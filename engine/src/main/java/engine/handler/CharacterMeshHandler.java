package engine.handler;

import engine.object.Mesh;
import engine.object.Primitive;
import org.joml.Vector2f;

import java.io.*;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterMeshHandler {
    public static final Pattern CHAR_ID_PATTERN = Pattern.compile("(id=)(\\d*)");
    public static final Pattern CHAR_X_PATTERN = Pattern.compile("(x=)(\\d*)");
    public static final Pattern CHAR_Y_PATTERN = Pattern.compile("(y=)(\\d*)");
    public static final Pattern CHAR_WIDTH_PATTERN = Pattern.compile("(width=)(\\d*)");
    public static final Pattern CHAR_HEIGHT_PATTERN = Pattern.compile("(height=)(\\d*)");
    public static final Pattern CHAR_SCALE_WIDTH_PATTERN = Pattern.compile("(scaleW=)(\\d*)");
    public static final Pattern CHAR_SCALE_HEIGHT_PATTERN = Pattern.compile("(scaleH=)(\\d*)");
    public static final Pattern CHAR_XADVANCE_PATTERN = Pattern.compile("(xadvance=)(\\d*)");
    public static final Pattern KERNING_FIRST_PATTERN = Pattern.compile("(first=)(-?\\d*)");
    public static final Pattern KERNING_SECOND_PATTERN = Pattern.compile("(second=)(-?\\d*)");
    public static final Pattern KERNING_AMOUNT_PATTERN = Pattern.compile("(amount=)(-?\\d*)");
    public static final Pattern CHAR_YOFFSET_PATTERN = Pattern.compile("(yoffset=)(-?\\d*)");
    private static final ThreadLocal<CharacterMeshHandler> INSTANCE = ThreadLocal.withInitial(CharacterMeshHandler::new);
    private static final String FONT_RESOURCE_FOLDER = "font/";
    private final Map<Character, Mesh> charMeshMap = new HashMap<>();
    private final Map<Character, Vector2f> charDimensionMap = new HashMap<>();
    private final Map<Character, Float> charXAdvanceMap = new HashMap<>();
    private final Map<KerningPair, Float> kerningMap = new HashMap<>();

    private CharacterMeshHandler() {
        InputStream inputStream = getInputStreamForFontDefinition();
        if (inputStream == null) {
            System.err.println("No font definition file found!");
            System.exit(1);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            float scaleWidth = 1;
            float scaleHeight = 1;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("common ")) {
                    scaleWidth = getScaleWidth(line);
                    scaleHeight = getScaleHeight(line);
                }
                if (line.startsWith("char ")) {
                    getCharInformation(line, scaleWidth, scaleHeight);
                }
                if (line.startsWith("kerning ")) {
                    getKerningInformation(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read font definition file! " + e.getMessage());
            System.exit(1);
        }
    }

    private void getCharInformation(String line, float scaleWidth, float scaleHeight) {
        float[] vertices;
        int[] indices = Primitive.QUAD_INDICES;
        float[] texture;

        Matcher charIdMatcher = CHAR_ID_PATTERN.matcher(line);
        charIdMatcher.find();
        char id = (char) Integer.parseInt(charIdMatcher.group(2));
        Matcher charXMatcher = CHAR_X_PATTERN.matcher(line);
        charXMatcher.find();
        float x = Float.parseFloat(charXMatcher.group(2));
        Matcher charYMatcher = CHAR_Y_PATTERN.matcher(line);
        charYMatcher.find();
        float y = Float.parseFloat(charYMatcher.group(2));
        Matcher charWidthMatcher = CHAR_WIDTH_PATTERN.matcher(line);
        charWidthMatcher.find();
        float width = Float.parseFloat(charWidthMatcher.group(2));
        Matcher charHeightMatcher = CHAR_HEIGHT_PATTERN.matcher(line);
        charHeightMatcher.find();
        float height = Float.parseFloat(charHeightMatcher.group(2));
        Matcher charXAdvanceMatcher = CHAR_XADVANCE_PATTERN.matcher(line);
        charXAdvanceMatcher.find();
        float offset = Float.parseFloat(charXAdvanceMatcher.group(2));
        Matcher yOffsetMatcher = CHAR_YOFFSET_PATTERN.matcher(line);
        yOffsetMatcher.find();
        float yOffset = Float.parseFloat(yOffsetMatcher.group(2));

        vertices = new float[]{
                -width / 2, (height / 2), -1.0f,
                width / 2, (height / 2), -1.0f,
                width / 2, (-height / 2), -1.0f,
                -width / 2, (-height / 2), -1.0f
        };
        texture = new float[]{
                x / scaleWidth, y / scaleHeight,
                (x + width) / scaleWidth, y / scaleHeight,
                (x + width) / scaleWidth, (y + height) / scaleHeight,
                x / scaleWidth, (y + height) / scaleHeight
        };
        Mesh mesh = new Mesh(vertices, indices, texture);
        mesh.loadMesh();
        this.charMeshMap.put(id, mesh);
        this.charDimensionMap.put(id, new Vector2f(width, height));
        this.charXAdvanceMap.put(id, offset);
    }

    private float getScaleHeight(String line) {
        float scaleHeight;
        Matcher charScaleHeightMatcher = CHAR_SCALE_HEIGHT_PATTERN.matcher(line);
        charScaleHeightMatcher.find();
        scaleHeight = Float.parseFloat(charScaleHeightMatcher.group(2));
        return scaleHeight;
    }

    private float getScaleWidth(String line) {
        float scaleWidth;
        Matcher charScaleWidthMatcher = CHAR_SCALE_WIDTH_PATTERN.matcher(line);
        charScaleWidthMatcher.find();
        scaleWidth = Float.parseFloat(charScaleWidthMatcher.group(2));
        return scaleWidth;
    }

    private void getKerningInformation(String line) {
        Matcher kerningFirstMatcher = KERNING_FIRST_PATTERN.matcher(line);
        kerningFirstMatcher.find();
        char kerningFirstChar = (char) Integer.parseInt(kerningFirstMatcher.group(2));

        Matcher kerningSecondMatcher = KERNING_SECOND_PATTERN.matcher(line);
        kerningSecondMatcher.find();
        char kerningSecondChar = (char) Integer.parseInt(kerningSecondMatcher.group(2));

        Matcher kerningAmountMatcher = KERNING_AMOUNT_PATTERN.matcher(line);
        kerningAmountMatcher.find();
        float kerningAmount = Float.parseFloat(kerningAmountMatcher.group(2));

        this.kerningMap.put(new KerningPair(kerningFirstChar, kerningSecondChar), kerningAmount);

    }

    public static CharacterMeshHandler getInstance() {
        return INSTANCE.get();
    }

    public Mesh getMeshForKey(char key) {
        if (!charMeshMap.containsKey(key)) {
            return charMeshMap.get('@');
        }
        return charMeshMap.get(key);
    }

    private InputStream getInputStreamForFontDefinition() {
        InputStream inputStream = null;
        try {
            Enumeration<JarEntry> entries = ((JarURLConnection) getClass().getClassLoader()
                    .getResource("font/").toURI().toURL().openConnection())
                    .getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(FONT_RESOURCE_FOLDER) && entry.getName().endsWith(".fnt")) {
                    String filename = entry.getName().split("/")[1];
                    inputStream = CharacterMeshHandler.class
                            .getClassLoader()
                            .getResourceAsStream(FONT_RESOURCE_FOLDER + filename);
                    break;
                }
            }
        } catch (Exception e) {
            File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(FONT_RESOURCE_FOLDER).getPath());
            for (File file : templateDirectory.listFiles()) {
                inputStream = CharacterMeshHandler.class
                        .getClassLoader()
                        .getResourceAsStream(FONT_RESOURCE_FOLDER + file.getName());
            }
        }
        return inputStream;
    }

    public Vector2f getDimensionForChar(char character) {
        if (this.charDimensionMap.containsKey(character)) {
            return this.charDimensionMap.get(character);
        }
        return this.charDimensionMap.get('@');
    }

    public float getXAdvanceForChar(char character) {
        if (this.charXAdvanceMap.containsKey(character)) {
            return this.charXAdvanceMap.get(character);
        }
        return this.charXAdvanceMap.get('@');
    }

    public float getKerningForCharAAfterCharB(char charA, char charB) {
        KerningPair kerningPair = new KerningPair(charA, charB);
        if (this.kerningMap.containsKey(kerningPair)) {
            return this.kerningMap.get(kerningPair);
        }
        return 0.0f;
    }

    private static class KerningPair {

        private char a;
        private char b;

        public KerningPair(char a, char b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KerningPair that = (KerningPair) o;
            return a == that.a && b == that.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
