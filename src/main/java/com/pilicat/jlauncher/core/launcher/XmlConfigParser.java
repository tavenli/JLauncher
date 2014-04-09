package com.pilicat.jlauncher.core.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pilicat.jlauncher.core.exception.ConfigurationException;
import com.pilicat.jlauncher.core.utils.XmlUtils;

/**
 * Event based launcher configuration parser, delegating effective configuration handling to ConfigurationHandler.
 *
 */
public class XmlConfigParser extends ConfigurationParser{

	public XmlConfigParser(ConfigurationHandler handler, Properties systemProperties) {
		super(handler, systemProperties);
		
	}

	@Override
	public void parse(InputStream is) throws Exception {
		
		Document xmldoc = XmlUtils.xml(is);
		//Element element = xmldoc.getElementById("AppLibs");
		//List<Element> libs = XmlUtils.childrens(element);

		String appName = XmlUtils.get((Element)xmldoc.getFirstChild(), "AppName");
		String mainClass = XmlUtils.get((Element)xmldoc.getFirstChild(), "MainClass");

        this.handler.setAppMain( mainClass, appName );
        handler.addRealm( appName );
        
        
		List<String> libPathList = new ArrayList<String>();
		NodeList libs = xmldoc.getElementsByTagName("LibPath");
		int libsLen = libs.getLength(); 
		for (int i = 0; i < libsLen; i++) {
        	Node node = libs.item(i);
            if (node instanceof Element){
            	Element element = (Element) node;
            	//element.getTagName();
            	//element.getNodeName();
            	//element.getNodeValue();
            	String nodeVal = element.getTextContent();
            	libPathList.add(nodeVal);
            	//
            	loadLibs(nodeVal);
            }
        }
		
		
		
	}
	
	private void loadLibs(String libPath) throws MalformedURLException, FileNotFoundException, ConfigurationException{
		//
		libPath = filter(libPath);
		
        if ( libPath.indexOf( "*" ) >= 0 )
        {
            loadGlob( libPath, false /*not optionally*/ );
        }
        else
        {
            File file = new File( libPath );

            if ( file.exists() )
            {
                handler.addLoadFile( file );
            }
            else
            {
                try
                {
                  handler.addLoadURL( new URL( libPath ) );
                }
                catch ( MalformedURLException e )
                {
                    throw new FileNotFoundException( libPath );
                }
            }
        }
		
	}
	
	private String filter( String text )   throws ConfigurationException
        {
            String result = "";

            int cur = 0;
            int textLen = text.length();

            int propStart = -1;
            int propStop = -1;

            String propName = null;
            String propValue = null;

            while ( cur < textLen )
            {
                propStart = text.indexOf( "${", cur );

                if ( propStart < 0 )
                {
                    break;
                }

                result += text.substring( cur, propStart );

                propStop = text.indexOf( "}", propStart );

                if ( propStop < 0 )
                {
                    throw new ConfigurationException( "Unterminated property: " + text.substring( propStart ) );
                }

                propName = text.substring( propStart + 2, propStop );

                propValue = systemProperties.getProperty( propName );

                /* do our best if we are not running from surefire */
                if ( propName.equals( "basedir" ) && ( propValue == null || propValue.equals( "" ) ) )
                {
                    propValue = ( new File( "" ) ).getAbsolutePath();

                }

                if ( propValue == null )
                {
                    throw new ConfigurationException( "No such property: " + propName );
                }
                result += propValue;

                cur = propStop + 1;
            }

            result += text.substring( cur );

            return result;
        }
    
	
	private void loadGlob(String line, boolean optionally)
			throws MalformedURLException, FileNotFoundException,
			ConfigurationException {
		File globFile = new File(line);

		File dir = globFile.getParentFile();
		if (!dir.exists()) {
			if (optionally) {
				return;
			} else {
				throw new FileNotFoundException(dir.toString());
			}
		}

		String localName = globFile.getName();

		int starLoc = localName.indexOf("*");

		final String prefix = localName.substring(0, starLoc);

		final String suffix = localName.substring(starLoc + 1);

		File[] matches = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (!name.startsWith(prefix)) {
					return false;
				}

				if (!name.endsWith(suffix)) {
					return false;
				}

				return true;
			}
		});

		for (File match : matches) {
			handler.addLoadFile(match);
		}
	}
	

	

}
