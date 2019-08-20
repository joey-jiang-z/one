/**
* <p>Title: Test.java</p>
* <p>Description: </p>
* @author jiangzheng
* @date 2019年7月5日 下午5:24:48
* @version 1.0
*/
package com.one.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Description: TODO
 * @author jiangzheng
 * @date 2019年7月5日 下午5:24:48
 */
public class MyURLClassLoader {

	/**
	 * @Description: TODO
	 * @param args   
	 * @return void  
	 * @throws
	 * @author jiangzheng
	 * @date 2019年7月5日 下午5:24:49
	 */
	public static void main(String args[]) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		File file = new File("D:/test/LoadModel.class");

		URL url = file.toURI().toURL();
		URLClassLoader loader = new URLClassLoader(new URL[] { url });
		Class c = loader.loadClass("com.one.classloader.LoadModel");
		Object o = c.newInstance();
		Comparable comparable = (Comparable) o;
		System.out.println("result:" + comparable.compareTo(""));
	}
}

class LoadModel implements Comparable {
	public int compareTo(Object o) {
		return 33;
	}

}
