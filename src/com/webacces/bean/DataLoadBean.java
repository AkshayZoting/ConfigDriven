package com.webacces.bean;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.webacces.util.XMLParser;
import com.webaccess.sessionhelper.SessionHelp;

@ViewScoped
@ManagedBean(name = "dataLoadBean")
public class DataLoadBean {
	
	private UploadedFile file;
	private List<String> masterAxis = new ArrayList<>();
	private List<String> xAxis = new ArrayList<>();
	private List<String> yAxis = new ArrayList<>();
	private String xSelectedAxis = "";
	private String ySelectedAxis = "";
	private List<String> xSeries = new ArrayList<>();
	private List<String> ySeries = new ArrayList<>();
	List<LinkedHashMap<String,String>> rowData = new ArrayList<>();
	List<LinkedHashMap<String,String>> columnDef = new ArrayList<>();
	private String fileName;
	private String message;
	private String selectedDataFile = "";
	private List<String> dataFileList = new ArrayList<>();
	String userPath = "";
	String selectedfilePath;
	String selectedfilename;
	
	public DataLoadBean(){
		HttpSession session = SessionHelp.getSession();
		userPath = "C:/data/"+(String) session.getAttribute("name");
		checkifPathExist(userPath);
		dataFileList = getUsersDataFileList(userPath);
	}
	
	
	public void upload() {
        if (file != null) {
        	  FacesMessage message = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
	            FacesContext.getCurrentInstance().addMessage(null, message);
        	String rowData = processFile();
        	String columns = generateColumns();
        	if(!rowData.equals("")){
        		PrimeFaces.current().executeScript("initGridData("+ columns +","+ rowData +")");
        		xAxis.addAll(new ArrayList<>(masterAxis));
        		yAxis.addAll(new ArrayList<>(masterAxis));
        		xSelectedAxis = xAxis.get(0);
        		ySelectedAxis = yAxis.get(1);
        		graphDataUpdate();
        	}
        }
    }
	
		
	
	public String processFile(){
		String rows = "";
		CSVReader reader = null;
		try {
			rowData = new ArrayList<>();
			reader = new CSVReader(new InputStreamReader(file.getInputStream()));
			String[] header = reader.readNext();
			masterAxis = Arrays.asList(header);
			String[] nextLine;
			// read one line at a time
			while ((nextLine = reader.readNext()) != null) {
				int count = 0;
				LinkedHashMap<String,String> dataMap = new LinkedHashMap<>();
				for (String token : nextLine) {
					dataMap.put(header[count],token);
					count ++;
				}
				rowData.add(dataMap);
			}
			rows = new Gson().toJson(rowData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}
	
	public String generateColumns(){
		String column = "";
		if(masterAxis != null && !masterAxis.isEmpty()){
			for (String header : masterAxis) {
				LinkedHashMap<String,String> map =new LinkedHashMap<>();
				map.put("field", header);
				columnDef.add(map);
			}
			column = new Gson().toJson(columnDef);
		}
		return column;
	}
	
	public void graphDataUpdate(){
		if(!xSelectedAxis.equals(ySelectedAxis)){
			 xSeries=getSeries(xSelectedAxis, xSeries);
			 ySeries=getSeries(ySelectedAxis, ySeries);
			PrimeFaces.current().executeScript("displayHighCharts('"+ xSelectedAxis +"','"+ ySelectedAxis +"','"+ xSeries +"','"+ ySeries +"')");
		}
		else{
			return;
		}
		
	}
	
	public List<String> getSeries(String selectedAxis,List<String> series){
		series = new ArrayList<>();
		for (LinkedHashMap<String, String> map : rowData) {
			if (map.get(selectedAxis) != null) {
				series.add(map.get(selectedAxis));
			}
		}
		return series;
	}
	
	
	public void saveGridData(){
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String data = params.get("gridData");
		if (data != null && !data.equals("")) {
			List<LinkedHashMap<String, String>> gridData = new Gson().fromJson(data, new TypeToken<List<LinkedHashMap<String, String>>>(){}.getType());
			if(gridData != null){
				String filePath = userPath + "/"+fileName+".xml";
				XMLParser.createUploadDataFile(gridData, filePath);
				File dataFile = new File(filePath);
				if(dataFile.exists()){
					message = "File created successfully";
				}
			}
		}
	}
	
		
	public List<String> getUsersDataFileList(String userPath) {
		List<String> filesList = new ArrayList<>();
		
		try {
			// Creating a File object for directory
			File directoryPath = new File(userPath);
			// List of all files and directories
			File filesListDemo[] = directoryPath.listFiles();
		//	filesList = Arrays.asList(directoryPath.getName());
			for(File file : filesListDemo) {
		       //  System.out.println("File name: "+file.getName());
		         filesList.add(file.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filesList;
	}
	
	/*public void loadFileData(){
		HttpSession session = SessionHelp.getSession();
		selectedfilePath = "C:/data/"+(String) session.getAttribute("name")+ selectedDataFile;
		if (selectedDataFile != null && !selectedDataFile.equals("")) {
			try {
				File inputFile = new File(selectedfilePath);           //give path of file
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = factory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();
				XPath xPath = XPathFactory.newInstance().newXPath();
		String exp = "/Rows/Row";
				NodeList nList = (NodeList) xPath.compile(exp).evaluate(doc, XPathConstants.NODESET);
				List<String>  rowd = new ArrayList<>();

				  for (int i = 0; i < nList.getLength(); i++) {
			            Node node = nList.item(i);
			            
			            if (node.getNodeType() == Node.ELEMENT_NODE)
				         {
				            Element elem = (Element) node;
				              rowd.setRollno(Integer.parseInt(elem.getAttribute("Row")));
				           
				            .add(employee);
				         }
				      }
			         
			         String rowData = new Gson().toJson(employees);
			      System.out.println(rowData);
			      
			     
			         PrimeFaces.current().executeScript("initgriddata("+rowData+");");
				 // return employees;
				   }
			
				
			}catch (Exception e) {
				e.printStackTrace();
			}}
	}*/
		
	public void checkifPathExist(String folder){
		File file = new File(folder);
		if(!file.exists()){
			file.mkdir();
		}
	}
	
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<String> getxAxis() {
		return xAxis;
	}

	public void setxAxis(List<String> xAxis) {
		this.xAxis = xAxis;
	}

	public List<String> getyAxis() {
		return yAxis;
	}

	public void setyAxis(List<String> yAxis) {
		this.yAxis = yAxis;
	}

	public String getxSelectedAxis() {
		return xSelectedAxis;
	}

	public void setxSelectedAxis(String xSelectedAxis) {
		this.xSelectedAxis = xSelectedAxis;
	}

	public String getySelectedAxis() {
		return ySelectedAxis;
	}

	public void setySelectedAxis(String ySelectedAxis) {
		this.ySelectedAxis = ySelectedAxis;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}



	public String getSelectedDataFile() {
		return selectedDataFile;
	}



	public void setSelectedDataFile(String selectedDataFile) {
		this.selectedDataFile = selectedDataFile;
	}



	public List<String> getDataFileList() {
		return dataFileList;
	}



	public void setDataFileList(List<String> dataFileList) {
		this.dataFileList = dataFileList;
	}


	public String getSelectedfilename() {
		return selectedfilename;
	}


	public void setSelectedfilename(String selectedfilename) {
		this.selectedfilename = selectedfilename;
	}
	
	

}
