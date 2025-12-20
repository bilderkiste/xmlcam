package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;

import main.Main;

public class GCodeDialect {
	
	private String dialect;
	private Dialect obj;
	
    public GCodeDialect(Settings settings) {
    	dialect = settings.getDialect();
    	Yaml yaml = new Yaml();

		try {
			InputStream inputStream = new FileInputStream(new File("dialects/" + dialect + ".yaml"));
	    	obj = yaml.loadAs(inputStream, Dialect.class);
	    	System.out.println(obj);
		} catch (FileNotFoundException e) {
			Main.log.log(Level.SEVERE, "Failed to load {0}.yaml. {1}", new Object[] { dialect, e });	
		} catch (Exception e) {
			Main.log.log(Level.SEVERE, "Error parsing yaml file. {0}", new Object[] { e });
		}
    }
    
    public String getName() {
        return (String) obj.name;
    }

    public ArrayList<String> getLines(String section) {
        return obj.sections.get(section);
    }
    
    public static class Dialect {
        public String name;
        public Map<String, ArrayList<String>> sections;

        public Dialect() {}
    }

}
