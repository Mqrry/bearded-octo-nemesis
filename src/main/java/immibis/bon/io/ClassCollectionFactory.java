package immibis.bon.io;

import immibis.bon.ClassCollection;
import immibis.bon.ClassFormatException;
import immibis.bon.IProgressListener;
import immibis.bon.NameSet;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ClassCollectionFactory {
	public static ClassCollection loadClassCollection(NameSet ns, File from, IProgressListener progress) throws IOException, ClassFormatException {
		if(from.isDirectory()) {
			Collection<ClassNode> classes = new ArrayList<ClassNode>();
			loadFromDir("", from, classes);
			return new ClassCollection(ns, classes, null);
		}
		else
			return JarLoader.loadClassesFromJar(ns, from, progress);
	}
	
	private static void loadFromDir(String prefix, File dir, Collection<ClassNode> result) throws IOException, ClassFormatException {
		if(dir.isDirectory()) {
			
			if(!prefix.equals(""))
				prefix += "/";
			
			for(String fn : dir.list()) {
				loadFromDir(prefix + fn, new File(dir, fn), result);
			}
			
		} else if(prefix.endsWith(".class")) {
			{
				FileInputStream in = new FileInputStream(dir);
				ClassNode cn = IOUtils.readClass(IOUtils.readStreamFully(in));
				in.close();
				
				if(!prefix.equals(cn.name + ".class"))
					throw new ClassFormatException("Class '"+cn.name+"' has wrong path in folder: '"+prefix+"'");
				
				result.add(cn);
			}
		}
	}
}
