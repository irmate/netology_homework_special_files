import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {

    public static void writeCSV(String fileName) {
        List<String[]> listOfEmployee = new ArrayList<>();
        listOfEmployee.add("1,John,Smith,USA,25".split(","));
        listOfEmployee.add("2,Inav,Petrov,RU,23".split(","));
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName, false))) {
            writer.writeAll(listOfEmployee);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseCSV(String[] columMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeXML(String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element staff = document.createElement("staff");
            document.appendChild(staff);
            Element employee = document.createElement("employee");
            staff.appendChild(employee);
            Element id = document.createElement("id");
            id.appendChild(document.createTextNode("1"));
            employee.appendChild(id);
            Element firstName = document.createElement("firstName");
            firstName.appendChild(document.createTextNode("John"));
            employee.appendChild(firstName);
            Element lastName = document.createElement("lastName");
            lastName.appendChild(document.createTextNode("Smith"));
            employee.appendChild(lastName);
            Element country = document.createElement("country");
            country.appendChild(document.createTextNode("USA"));
            employee.appendChild(country);
            Element age = document.createElement("age");
            age.appendChild(document.createTextNode("25"));
            employee.appendChild(age);
            Element employee1 = document.createElement("employee");
            staff.appendChild(employee1);
            Element id1 = document.createElement("id");
            id1.appendChild(document.createTextNode("2"));
            employee1.appendChild(id1);
            Element firstName1 = document.createElement("firstName");
            firstName1.appendChild(document.createTextNode("Inav"));
            employee1.appendChild(firstName1);
            Element lastName1 = document.createElement("lastName");
            lastName1.appendChild(document.createTextNode("Petrov"));
            employee1.appendChild(lastName1);
            Element country1 = document.createElement("country");
            country1.appendChild(document.createTextNode("RU"));
            employee1.appendChild(country1);
            Element age1 = document.createElement("age");
            age1.appendChild(document.createTextNode("23"));
            employee1.appendChild(age1);
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(fileName));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> read(Node node) {
        List<Employee> employees = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (node_.getNodeName().equals("employee")) {
                long id = 0L;
                String firstName = "";
                String lastName = "";
                String country = "";
                int age = 0;
                NodeList employeeNodes = node_.getChildNodes();
                for (int j = 0; j < employeeNodes.getLength(); j++) {
                    Node empNode = employeeNodes.item(j);
                    if (empNode.getNodeName().equals("id")) {
                        id = Long.parseLong(empNode.getTextContent());
                    }
                    if (empNode.getNodeName().equals("firstName")) {
                        firstName = empNode.getTextContent();
                    }
                    if (empNode.getNodeName().equals("lastName")) {
                        lastName = empNode.getTextContent();
                    }
                    if (empNode.getNodeName().equals("country")) {
                        country = empNode.getTextContent();
                    }
                    if (empNode.getNodeName().equals("age")) {
                        age = Integer.parseInt(empNode.getTextContent());
                    }
                }
                employees.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return employees;
    }

    public static List<Employee> parseXML(String fileName) {
        Node node = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            node = doc.getDocumentElement();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return read(node);
    }

    public static String readString(String fileName) {
        String json = "";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            json = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> employees = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray jsonArray = (JsonArray) jsonElement;
        for (JsonElement e : jsonArray) {
            Gson gson = new Gson();
            employees.add(gson.fromJson(e, Employee.class));
        }
        return employees;
    }

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        writeCSV(fileName);
        List<Employee> listFromCSV = parseCSV(columnMapping, fileName);
        String jsonFromCSV = listToJson(listFromCSV);
        writeString(jsonFromCSV, "data.json");
        writeXML("data.xml");
        List<Employee> listFromXML = parseXML("data.xml");
        String jsonFromXML = listToJson(listFromXML);
        writeString(jsonFromXML, "data2.json");
        String json = readString("data.json");
        List<Employee> list = jsonToList(json);
        for (Employee employee : list) {
            System.out.println(employee);
        }
    }
}