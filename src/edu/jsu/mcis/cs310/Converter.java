package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.util.List;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
         String result = "{}"; // default return value; replace later!
        
        try {
        
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> lines = reader.readAll();
            String[] headers = lines.get(0);
            
            JsonArray colHeadings = new JsonArray();
            JsonArray prodNum = new JsonArray();
            JsonArray allData = new JsonArray();
            
            JsonObject json = new JsonObject();
            
            for(int j = 0; j < headers.length ; j++) {
                colHeadings.add(headers[j]);
            }

            for (int i = 1; i < lines.size(); i++) {               
                String[] values = lines.get(i); 
                prodNum.add(values[0]);               
                JsonArray data = new JsonArray();
                for(int k=1; k < values.length;k++){
                    if (k == colHeadings.indexOf("Season") || k == colHeadings.indexOf("Episode")) {
                        data.add(Integer.valueOf(values[k])); 
                    }
                    else {
                        data.add(values[k]); 
                    }
                }    
                allData.add(data);
            }
 
            json.put("ProdNums", prodNum);
            json.put("ColHeadings", colHeadings);
            json.put("Data", allData);     
           
            result = Jsoner.serialize(json);
        }
        
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
    
    }
    
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString){
        
        String result = ""; 
        DecimalFormat decimalFormat = new DecimalFormat("00");
        
        try {
           
            JsonObject JsonObject = Jsoner.deserialize(jsonString, new JsonObject());
            
            JsonArray colHeading = new JsonArray();
            colHeading = (JsonArray) (JsonObject.get("ColHeadings"));
            
            JsonArray prodNum = new JsonArray();
            prodNum = (JsonArray) (JsonObject.get("ProdNums"));
            
            JsonArray allData = new JsonArray();
            allData = (JsonArray) (JsonObject.get("Data"));
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter CSVWriter = new CSVWriter(stringWriter, ',', '"', '\\', "\n");
            
            String[] eachHeading = new String[colHeading.size()];
            for (int i = 0; i < colHeading.size(); i++) {
                eachHeading[i] = colHeading.get(i).toString();
            }
            CSVWriter.writeNext(eachHeading);
            
            for(int i = 0;i < prodNum.size();i++){
                String[] row = new  String[colHeading.size()];
                JsonArray eachData; 
                eachData = new JsonArray();
                eachData = (JsonArray)allData.get(i);
                
                row[0] = prodNum.get(i).toString();
                for (int j = 0; j < eachData.size(); j++) {
                    if(eachData.get(j) == eachData.get(colHeading.indexOf("Episode") - 1)){
                        int number = Integer.parseInt(eachData.get(j).toString());
                        String formattedNumber = "";
                        formattedNumber = decimalFormat.format(number);
                        row[j + 1] = formattedNumber;
                    }
                    
                    else{
                        row[j + 1] = eachData.get(j).toString();
                    }
                }
                CSVWriter.writeNext(row);
            }
            result = stringWriter.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return result.trim();
    }
    
}