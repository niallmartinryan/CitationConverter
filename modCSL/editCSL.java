import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;                                             
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import org.w3c.dom.Attr;

public class editCSL{

  public static void main(String argv[]){

    ArrayList<String> styles = getStyleNames();
    DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
    
    
    for(String style : styles){
      try{

        String finalFilePath ="";
        String filePath = "../../Resources/niall-styles/";
        if(style.length()>=4){
          String substr = style.substring(style.length() - 4);
         if(substr.equals(".csl")){
           finalFilePath = filePath + style; 
          }else{
            finalFilePath = filePath + style + ".csl" ;
          }
        }else{
         finalFilePath = filePath + style + ".csl";
        }
        System.out.println("finalFilePath : "+ finalFilePath);
        //System.exit(1);
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();    
        Document doc = docBuilder.parse(finalFilePath);

        // get root element
        Node root = doc.getFirstChild();
        // get childNodes
        NodeList list = root.getChildNodes();
        for( int i=0; i< list.getLength(); i++){

          Node node  = list.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element elem = (Element) node;
            //System.out.println("getNodeName : " + node.getNodeName());	
            if("bibliography".equals(node.getNodeName())){
              //System.out.println("found node heheh");
              addAnnotations(node);
              //NodeList myList = node.getChildNodes();
              try{
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("styles/"+style+".csl"));
                transformer.transform(source, result);
                //System.exit(1);
              } catch(TransformerException e){
                  System.out.println(e);
              }
            }
            //if(elem.hasAttribute("variable") && elem.getAttribute("variable").equals("author")){
              //System.out.println("Heheh found the right attribute");
           // }
          }else{
              System.out.println("This doesnt have a bibliography field");
          }
        }

      } catch (ParserConfigurationException pce) {
        pce.printStackTrace();
      } //catch (TransformerException tfe) {
      //	tfe.printStackTrace();
      //}
      catch (IOException ioe) {
        ioe.printStackTrace();
      } catch (SAXException sae) {
        sae.printStackTrace();
      }
      //System.exit(1);
    }            

  }
  public static ArrayList<String> getStyleNames(){
    File folder = new File(Resources.STYLES_LIBRARY_PATH_1);
    File folderInner = new File(Resources.STYLES_LIBRARY_PATH_2);
    File[] fileList2 = folderInner.listFiles();
    File[] fileList1 = folder.listFiles();
    ArrayList<String> styles = new ArrayList<String>();
    // Run through outer file Styles
    for (int i = 0; i < fileList1.length; i++) {
      String fileName = fileList1[i].getName();
      if (fileList1[i].isFile() &&
          fileName.substring(fileName.length() - 4).equals(Resources.FILE_ENDING_CSL)) {
        // System.out.println("File " + fileList1[i].getName());
        styles.add(fileName.substring(0, fileName.length() - 4));
      } else if (fileList1[i].isDirectory()) {
        // System.out.println("Directory " + fileList1[i].getName());
      }
    }
    // Run through inner file Styles
    for (int i=0;i<fileList2.length;i++ ) {
      String fileName = fileList2[i].getName();
      if(fileList2[i].isFile()){
        styles.add(fileName);
      }
    }
    return removeDubs(styles);
  }
  public static void addAnnotations(Node node) {
    // do something with the current node instead of System.out
    //System.out.println(node.getNodeName());

    NodeList nodeList = node.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node currentNode = nodeList.item(i);
      if(currentNode.getNodeName().equals("text")){
        //listAllAttributes((Element) currentNode);
        // firstly check if the element contains attrs prefix or suffix
        Element elem = (Element) currentNode;
        //suffix of prefix hehehe
        String oldPrefix = "";
        String oldSuffix = "";
        if(elem.hasAttribute("prefix")){
          oldPrefix = elem.getAttribute("prefix");

          // add the current prefix to old prefix	
        }
        if(elem.hasAttribute("suffix")){
          oldSuffix = elem.getAttribute("suffix");
          // add the current suffix to the old suffix
        }
        String macro = "";
        String endMacro = "";
        if(elem.hasAttribute("variable")){
          macro = " $" + elem.getAttribute("variable") + "$ ";
          endMacro = " $" + elem.getAttribute("variable") + "$ ";
        }else{
        // get whatever the macro text is and use that as the prefix $$
        macro = " $" + elem.getAttribute("macro") + "$ ";
        endMacro = " $/" + elem.getAttribute("macro") + "$ ";	 	
        }
        //System.out.println("macro : " + macro);
        //System.out.println("endMarco : " + endMacro);
        //System.out.println(" macro + prefix :: " + macro + oldPrefix);
        //System.out.println(" suffix + endMacro :: " + oldSuffix + endMacro);

        elem.setAttribute("prefix" , macro + oldPrefix);
        elem.setAttribute("suffix" , oldSuffix + endMacro);
        //System.out.println(currentNode.				
       // System.out.println("found one");
      }
      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
        //calls this method for all the children which is Element
        addAnnotations(currentNode);
      }
    }
  }
  public static ArrayList<String> removeDubs(ArrayList<String> arr ){
    Set<String> s = new LinkedHashSet<>(arr);
    List<String> list = new ArrayList<String>(s);
    return arr;
  }
  public static void listAllAttributes(Element element){
    System.out.println("list attributes of node: " + element.getNodeName());
    NamedNodeMap attributes = element.getAttributes();

    int numAttrs = attributes.getLength();
    for(int i=0;i< numAttrs; i++){
      Attr attr = (Attr) attributes.item(i);
      String attrName = attr.getNodeName();
      String attrValue = attr.getNodeValue();
      System.out.println("found attribute: " + attrName + " -- value : " + attrValue);

    }
  }
}                     
