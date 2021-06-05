package com.stackroute.datamunger.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stackroute.datamunger.query.DataTypeDefinitions;
import com.stackroute.datamunger.query.Header;

public class CsvQueryProcessor extends QueryProcessingEngine {
	public BufferedReader bufferedReader = null;
	public Header header = null;
	String dataRow ="";
	/*
	 * Parameterized constructor to initialize filename. As you are trying to
	 * perform file reading, hence you need to be ready to handle the IO Exceptions.
	 */
	
	public CsvQueryProcessor(String fileName) throws FileNotFoundException {
		bufferedReader = new BufferedReader(new FileReader(fileName));		
	}

	/*
	 * Implementation of getHeader() method. We will have to extract the headers
	 * from the first line of the file.
	 */

	@Override
	public Header getHeader() throws IOException {
		String[] headerNames =  null;
		if(header == null) {
			// read the first line
			String headerLine = bufferedReader.readLine();
			headerNames = headerLine .split(",");
			// populate the header object with the String array containing the header names
			header = new Header(headerNames);
		}
		return header;
	}

	/**
	 * This method will be used in the upcoming assignments
	 */
	@Override
	public void getDataRow() {
		try {
			dataRow = bufferedReader.readLine();
		} catch (IOException ioexception) {
			//System.out.println("ioexception in getDataRow");
		}
	}

	/*
	 * Implementation of getColumnType() method. To find out the data types, we will
	 * read the first line from the file and extract the field values from it. In
	 * the previous assignment, we have tried to convert a specific field value to
	 * Integer or Double. However, in this assignment, we are going to use Regular
	 * Expression to find the appropriate data type of a field. Integers: should
	 * contain only digits without decimal point Double: should contain digits as
	 * well as decimal point Date: Dates can be written in many formats in the CSV
	 * file. However, in this assignment,we will test for the following date
	 * formats('dd/mm/yyyy',
	 * 'mm/dd/yyyy','dd-mon-yy','dd-mon-yyyy','dd-month-yy','dd-month-yyyy','yyyy-mm
	 * -dd')
	 */
	
	@Override
	public DataTypeDefinitions getColumnType() throws IOException {
		if(dataRow.isBlank()){
			getDataRow();
		}
		String value = "";
		String dataType = "";
		String[] dataStringArray = dataRow.split(",");
		String[] dataTypes = new String[header.getHeaders().length];
		//to find the datatype
		for(int row = 0;row<dataStringArray.length;row++) {
			value = dataStringArray[row];
			// checking for Integer
			if(value.matches("[+-]?[0-9]+")) {
				dataType = "java.lang.Integer";
			}// checking for floating point numbers
			else if(value.matches("[+-]?[0-9]+[.][0-9]+")) {
				dataType = "java.lang.Double";
			}// checking for string
			else if(value.matches("[a-zA-Z0-9\s]+")) {
				dataType = "java.lang.String";
			}// checking for date format dd/mm/yyyy
			else if(value.matches("([0-2][0-9]|(3)[0-1])[/]((0)[1-9]|(1)[1-2])[/]([0-9]{4})") ||
					// checking for date format mm/dd/yyyy
					value.matches("((0)[1-9]|(1)[1-2])[/]([0-2][0-9]|(3)[0-1])[/]([0-9]{4})") ||
					// checking for date format dd-mon-yy
					value.matches("([0-2][0-9]|(3)[0-1])[-]([a-z]{3})[-]([0-9]{2})") ||
					// checking for date format dd-month-yy
					value.matches("([0-2][0-9]|(3)[0-1])[-]([a-z]+)[-]([0-9]{2})") ||
					// checking for date format dd-month-yyyy
					value.matches("([0-2][0-9]|(3)[0-1])[-]([a-z]+)[-]([0-9]{4})") ||
					// checking for date format yyyy-mm-dd
					value.matches("([0-9]{4})[-]((0)[1-9]|(1)[1-2])[-]([0-2][0-9]|(3)[0-1])")) {
				dataType ="java.util.Date";
			} else {
				dataType ="java.lang.Object";
			}
			dataTypes[row] = dataType;
		}
		int index = 0;
		for(String type:dataTypes) {
			if(type == null || type.isBlank()) {
				dataTypes[index] = "java.lang.Object";
			}
			index++;
		}
		DataTypeDefinitions dataTypeDef = new DataTypeDefinitions(dataTypes);
		return dataTypeDef;
	}

}
