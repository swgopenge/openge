package scripting;

import java.util.Iterator;
import java.util.Vector;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import main.Core;

public class ScriptingManager {
		
	public static PyObject callScript(String path, String module, String method) {
		PythonInterpreter python = new PythonInterpreter();
		python.cleanup();
		python.execfile(path + module + ".py");
		return python.get(method).__call__();
	}
	
	public static PyObject callScript(String path, String module, String method, Object ... args) {
		PythonInterpreter python = new PythonInterpreter();
		python.cleanup();
		python.execfile(path + module + ".py");
		PyObject[] pyArgs = new PyObject[args.length];
		for(int i = 0; i < args.length; i++) {
			pyArgs[i] = Py.java2py(args[i]);
		}
		return python.get(method).__call__(pyArgs);
	}
		
	public static PyObject getMethod(String path, String module, String method) {
		PythonInterpreter python = new PythonInterpreter();
		python.cleanup();
		python.execfile(path + module + ".py");
		PyObject func = python.get(method);
		return func;
	}
	
	public static String fetchString(String path, String method) {
		PyObject result = callScript(path, "", method);
		return ((PyObject)result).asString();		
	}
	
	public static int fetchInteger(String path, String method) {
		PyObject result = callScript(path, "", method);
		return ((PyObject)result).asInt();		
	}
	
	public static Vector<String> fetchStringVector(String path, String method) {
		Vector<String> vector = new Vector<String>();
		PyObject result = callScript(path, "", method);
		Iterable<PyObject> comp = (Iterable<PyObject>)result.asIterable();
		for (Iterator<PyObject> temp = comp.iterator(); temp.hasNext();){
			vector.add(temp.next().asString());
		}
		return vector;
	}
	
	public static Vector<Integer> fetchIntegerVector(String path, String method) {
		Vector<Integer> vector = new Vector<Integer>();
		PyObject result = callScript(path, "", method);
		Iterable<PyObject> comp = (Iterable<PyObject>)result.asIterable();
		for (Iterator<PyObject> temp = comp.iterator(); temp.hasNext();){
			vector.add(temp.next().asInt());
		}
		return vector;
	}
	
	public static Vector<Double> fetchDoubleVector(String path, String method) {
		Vector<Double> vector = new Vector<Double>();
		PyObject result = callScript(path, "", method);
		Iterable<PyObject> comp = (Iterable<PyObject>)result.asIterable();
		for (Iterator<PyObject> temp = comp.iterator(); temp.hasNext();){
			vector.add(temp.next().asDouble());
		}
		return vector;
	}


}
