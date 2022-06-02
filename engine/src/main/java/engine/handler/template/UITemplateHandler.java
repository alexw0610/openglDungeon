package engine.handler.template;

import engine.handler.Handler;
import engine.loader.YamlLoader;
import engine.loader.template.UITemplate;

import java.io.File;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

public class UITemplateHandler implements Handler<UITemplate> {
    private static final ThreadLocal<UITemplateHandler> INSTANCE = ThreadLocal.withInitial(UITemplateHandler::new);
    public static final String UI_TEMPLATE_FOLDER = "UITemplate/";

    private final Map<String, UITemplate> templateMap = new HashMap<>();

    public static UITemplateHandler getInstance() {
        return INSTANCE.get();
    }

    private UITemplateHandler() {
        try {
            Enumeration<JarEntry> entries = ((JarURLConnection) getClass().getClassLoader()
                    .getResource(UI_TEMPLATE_FOLDER).toURI().toURL().openConnection())
                    .getJarFile().entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(UI_TEMPLATE_FOLDER) && entry.getName().endsWith(".yaml")) {
                    String filename = entry.getName().split("/")[1];
                    UITemplate template = YamlLoader.load(UITemplate.class, UI_TEMPLATE_FOLDER + filename);
                    addObject(String.valueOf(template.getTemplateName()), template);
                }
            }
        } catch (Exception e) {
            File templateDirectory = new File(Thread.currentThread().getContextClassLoader().getResource(UI_TEMPLATE_FOLDER).getPath());
            for (File file : templateDirectory.listFiles()) {
                UITemplate template = YamlLoader.load(UITemplate.class, UI_TEMPLATE_FOLDER + file.getName());
                addObject(String.valueOf(template.getTemplateName()), template);
            }
        }
    }

    @Override
    public UITemplate getObject(String key) {
        return templateMap.get(key);
    }

    @Override
    public List<UITemplate> getAllObjects() {
        return null;
    }

    @Override
    public void addObject(String key, UITemplate object) {
        this.templateMap.put(key, object);
    }

    @Override
    public void addObject(UITemplate object) {
    }

    @Override
    public void removeObject(String key) {
        this.templateMap.remove(key);
    }
}
