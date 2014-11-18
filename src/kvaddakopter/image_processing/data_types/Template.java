package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;

public class Template {
	static ArrayList<Template> templates = new ArrayList<Template>();
	
	public int getNumDescriptors(){
		return 10;
	}
	
	static ArrayList<Template> getTemplates(){
		return templates;
	}
}
