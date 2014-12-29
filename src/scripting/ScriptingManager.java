package scripting;

import java.util.Vector;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public abstract class ScriptingManager {
	
	private static PyObject getMethod(String path, String module, String method) {	// Ziggy: This could probably be moved to callScript()
		PythonInterpreter python = new PythonInterpreter();							// since it's not used elsewhere.
		python.cleanup();
		python.execfile(path + module + ".py");
		
		return python.get(method);
	}
	
	public static PyObject callScript(String path, String module, String method, Object... args) {
		return getMethod(path, module, method).__call__(Py.javas2pys(args));
	}
	
	public static String fetchString(String path, String method) {
		return callScript(path, "", method).asString();		
	}
	
	public static int fetchInteger(String path, String method) {
		return callScript(path, "", method).asInt();		
	}
	
	public static Vector<String> fetchStringVector(String path, String method) {
		Vector<String> vector = new Vector<String>();
		
		for(PyObject py : callScript(path, "", method).asIterable())
			vector.add(py.asString());
		
		return vector;
	}
	
	public static Vector<Integer> fetchIntegerVector(String path, String method) {
		Vector<Integer> vector = new Vector<Integer>();
		
		for(PyObject py : callScript(path, "", method).asIterable())
			vector.add(py.asInt());
		
		return vector;
	}
	
	public static Vector<Double> fetchDoubleVector(String path, String method) {
		Vector<Double> vector = new Vector<Double>();
		
		for(PyObject py : callScript(path, "", method).asIterable())
			vector.add(py.asDouble());
		
		return vector;
	}


}
