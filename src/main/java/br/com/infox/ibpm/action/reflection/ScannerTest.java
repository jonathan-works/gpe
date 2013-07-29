/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.action.reflection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.itx.util.FileUtil;


public class ScannerTest {
	private Map<String,String> mapPrefixes = new HashMap<String, String>();
	
	public ScannerTest() {
		carregarPrefixos();
	}
	
	/**
	 * Carrega os prefixos para serem alterados.
	 */
	private void carregarPrefixos(){
		mapPrefixes.put("cd_", "cod_");
		mapPrefixes.put("nr_", "numero_");
		mapPrefixes.put("dt_", "data_");
		mapPrefixes.put("vl_", "valor_");
		mapPrefixes.put("nm_", "nome_");
		mapPrefixes.put("tp_", "tipo_");
		mapPrefixes.put("ds_", "");
		mapPrefixes.put("st_", "");
		mapPrefixes.put("in_", "");
		mapPrefixes.put("ob_", "");
		mapPrefixes.put("sg_", "");
		mapPrefixes.put("tb_", "");
	}
	
	/**
	 * Pega o nome da tabela do banco e retorna o nome de uma classe.
	 * @param tableName
	 * @return
	 */
	private String tableToClassName(String tableName){
		String result = tableName;
		
		Set<Entry<String,String>> entrySet = mapPrefixes.entrySet();
		for (Entry<String, String> entry : entrySet) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		
		String[] split = result.split("_");
		StringBuilder sb = new StringBuilder();
		for (String stringSplit : split) {
			sb.append(stringSplit.substring(0, 1).toUpperCase() + stringSplit.substring(1));
		}
		result = sb.toString();
		
		return result;
	}
	
	/**
	 * Pega o nome da coluna do banco e retorna o nome do método.
	 * @param columnName
	 * @return
	 */
	private String columnToPropertyName(String columnName){
		String result = columnName;
		
		Set<Entry<String,String>> entrySet = mapPrefixes.entrySet();
		for (Entry<String, String> entry : entrySet) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		
		
		String[] split = result.split("_");
		StringBuilder sb = new StringBuilder();
		for (String stringSplit : split) {
			sb.append(stringSplit.substring(0, 1).toUpperCase() + stringSplit.substring(1));
		}
		result = "get" + sb.toString();
		
		return result;
	}

	public void scan(ClassLoader classLoader, Set<String> locations,
			Set<String> packages) {
		if (!(classLoader instanceof URLClassLoader)) {
			return;
		}

		URLClassLoader urlLoader = (URLClassLoader) classLoader;
		URL[] urls = urlLoader.getURLs();

		for (URL url : urls) {
			String path = url.getFile();
			File location = null;
			try {
				location = new File(url.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return;
			}

			// Only process the URL if it matches one of our filter strings
			if (matchesAny(path, locations)) {
				if (location.isDirectory()) {
					getClassesInDirectory(null, location, packages);
				} else {
					getClassesInJar(location, packages);
				}
			}
		}
	}

	private void getClassesInDirectory(String parent, File location,
			Set<String> packagePatterns) {
		File[] files = location.listFiles();
		StringBuilder builder = null;

		for (File file : files) {
			builder = new StringBuilder(100);
			builder.append(parent).append(".").append(file.getName());
			String packageOrClass = (parent == null ? file.getName() : builder
					.toString());

			if (file.isDirectory()) {
				getClassesInDirectory(packageOrClass, file, packagePatterns);
			} else if (file.getName().endsWith(".class")) {
				if (matchesAny(packageOrClass, packagePatterns)) {
						
//Início dos procedimentos para achar a diferença
					String classeName = packageOrClass.replaceAll(".class", "");
					Object obj = null;
					try {
						obj = Class.forName(classeName).newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (obj != null) {
						Table tableAnnotation = obj.getClass().getAnnotation(javax.persistence.Table.class);
						Entity entityAnnotation = obj.getClass().getAnnotation(javax.persistence.Entity.class);
						if (entityAnnotation != null && tableAnnotation != null){
							String classNameCore = obj.getClass().getSimpleName();
							String classNameBase = tableToClassName(tableAnnotation.name());
							if (!classNameCore.equals(classNameBase)){
								System.out.println("(Class)"+classeName + ": " + classNameBase + " -> " + classNameCore);
							}
	
						}
	
						Method[] methods = obj.getClass().getMethods();
						for (Method method : methods) {
							Column columnAnnotation = method.getAnnotation(javax.persistence.Column.class);
							
							if (columnAnnotation != null){
								String columnNameCore = method.getName();
								String columnNameBase = columnToPropertyName(columnAnnotation.name());
								if (!columnNameCore.equals(columnNameBase)){
									System.out.println("(Method)"+classeName + ": " + columnNameBase + " -> " + columnNameCore);
								}
								
							}
						}
					}
//Fim dos procedimentos para achar a diferença 

				
				}
			}
		}
	}

	private void getClassesInJar(File location, Set<String> packagePatterns) {
		JarFile jar = null;
		try {
			jar = new JarFile(location);
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!entry.isDirectory() && name.endsWith(".class")) {
					if (matchesAny(name, packagePatterns)) {
						System.out.println(name);
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			FileUtil.close(jar);
		}
	}

	/**
	 * Filtrar por pacote.
	 * @param text
	 * @param filters
	 * @return
	 */
	private boolean matchesAny(String text, Set<String> filters) {
		if (filters == null || filters.size() == 0) {
			return true;
		}
		for (String filter : filters) {
			int indexOf = text.indexOf(filter);
			
			if (indexOf != -1 && text.replace(filter+".", "").replace(".class", "").
					indexOf(".") == -1) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Set<String> packs = new HashSet<String>();
		packs.add("br.com.infox.ibpm.entity");
		ScannerTest st = new ScannerTest();
		st.scan(Thread.currentThread().getContextClassLoader(),
				Collections.EMPTY_SET, packs);
	}
}