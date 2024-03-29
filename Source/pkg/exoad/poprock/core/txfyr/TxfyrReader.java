package pkg.exoad.poprock.core.txfyr;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pkg.exoad.poprock.core.debug.DebugService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public final class TxfyrReader
{
	private static final WeakHashMap<String,TxfyrCluster> __cache=new WeakHashMap<>();
	
	private TxfyrReader(){}
	
	public static Optional<ZipEntry[]> exportAllIdentifiersRaw(String path)
	{
		File f=new File(path);
		if(path.endsWith(Txfyr.getPackageFileExtension())&&f.canWrite()&&f.canRead()&&f.isFile()&&!f.isDirectory())
		{
			try(ZipFile zipFile=new ZipFile(path))
			{
				Enumeration<? extends ZipEntry> entries=zipFile.entries();
				ArrayList<ZipEntry> stash=new ArrayList<>();
				while(entries.hasMoreElements())
				{
					ZipEntry entry=entries.nextElement();
					if(entry
						.getName()
						.endsWith(Txfyr.getIdentifierFileExtension()))
						stash.add(entry);
				}
				return Optional.of(stash.toArray(new ZipEntry[0]));
			}catch(IOException e)
			{
				e.printStackTrace();
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	public static Optional<TxfyrCluster> exportAllShards(
		String path
	)
	{
		if(__cache.containsKey(path))
			return Optional.of(__cache.get(path)); // prayge to not be null :(
		File f=new File(path);
		if(path.endsWith(Txfyr.getPackageFileExtension())&&f.canWrite()&&f.canRead()&&f.isFile()&&!f.isDirectory())
		{
			try(ZipFile zipFile=new ZipFile(path))
			{
				Enumeration<? extends ZipEntry> entries=zipFile.entries();
				DocumentBuilderFactory builder=DocumentBuilderFactory.newInstance();
				DocumentBuilder db=builder.newDocumentBuilder();
				while(entries.hasMoreElements())
				{
					ZipEntry entry=entries.nextElement();
					if(entry
						   .getName()
						   .endsWith(Txfyr.getIdentifierFileExtension())&&!entry.isDirectory())
					{
						Document doc=db.parse(zipFile.getInputStream(entry));
						doc
							.getDocumentElement()
							.normalize();
						Element clusterRoot=doc.getDocumentElement();
						DebugService.panicOn(
							!Long
								.toString(Txfyr.VERSION)
								.equals(clusterRoot.getAttribute("Version")),
							"A txfyr cluster identifier was found to have an incompatible version! Got: "+clusterRoot.getAttribute(
								"Version")+" Required: "+Txfyr.VERSION
						);
						Element modelElement=(Element)clusterRoot
							.getElementsByTagName("Model")
							.item(0);
						String clusterName=modelElement
							.getElementsByTagName(
								"Name")
							.item(0)
							.getTextContent()
							.replaceAll("\\s+","");
						int clusterWidth=Integer.parseInt(modelElement
															  .getElementsByTagName(
																  "Width")
															  .item(0)
															  .getTextContent()
															  .trim());
						int clusterHeight=Integer.parseInt(modelElement
															   .getElementsByTagName(
																   "Height")
															   .item(0)
															   .getTextContent()
															   .trim());
						String clusterTarget=modelElement
							.getElementsByTagName("Target")
							.item(0)
							.getTextContent()
							.trim();
						LinkedHashSet<TxfyrShard> shardsCollect=new LinkedHashSet<>();
						// parse for all shards
						ZipEntry targetEntry=zipFile.getEntry(clusterTarget);
						if(targetEntry!=null) // according to the docs of ZipFile::getEntry
						// oh yea, i think all of the debugging related things are not really worth it
						{
							BufferedImage image=ImageIO.read(zipFile.getInputStream(
								targetEntry));
							DebugService.panicOn(
								clusterHeight>image.getHeight(),
								"The target for cluster: "+clusterName+" has a height specification("+clusterHeight+") that exceeds the target texture's height("+image.getHeight()+")"
							);
							DebugService.panicOn(
								clusterWidth>image.getWidth(),
								"The target for cluster: "+clusterName+" has a width specification("+clusterWidth+") that exceeds the target texture's width("+image.getWidth()+")"
							);
							Element shardElement=(Element)clusterRoot
								.getElementsByTagName("Shards")
								.item(0);
							NodeList shards=shardElement.getElementsByTagName("ShardEntry");
							for(int i=0;i<shards.getLength();i++)
							{
								Element shardEntry=(Element)shards.item(i);
								shardsCollect.add(new TxfyrShard(
									shardEntry.getAttribute("Name"),
									Integer.parseInt(shardEntry.getAttribute(
										"X")),
									Integer.parseInt(shardEntry.getAttribute(
										"Y")),
									Integer.parseInt(
										shardEntry.getAttribute(
											"Width")),
									Integer.parseInt(
										shardEntry.getAttribute(
											"Height"))
								));
							}
							TxfyrCluster res=new TxfyrCluster(
								clusterName,
								clusterWidth,
								clusterHeight,
								image,
								shardsCollect
							);
							__cache.put(path,res);
							return Optional.of(__cache.get(path));
						}
						else
							DebugService.panicWith(new FileNotFoundException(
								"Could not find cluster texture target: "+clusterTarget+" for cluster: "+clusterName));
					}
				}
			}catch(IOException|ParserConfigurationException|SAXException|
				   NumberFormatException e)
			{
				e.printStackTrace();
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}
